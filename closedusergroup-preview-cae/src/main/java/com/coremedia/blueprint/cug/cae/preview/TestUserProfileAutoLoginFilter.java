package com.coremedia.blueprint.cug.cae.preview;

import com.coremedia.blueprint.common.contentbeans.CMSymbol;
import com.coremedia.blueprint.cug.cae.CUGAuthorityLoader;
import com.coremedia.blueprint.elastic.social.cae.user.UserContext;
import com.coremedia.elastic.core.api.users.DuplicateEmailException;
import com.coremedia.elastic.core.api.users.DuplicateNameException;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.elastic.social.springsecurity.SocialAuthenticationToken;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.PropertyProvider;
import com.coremedia.personalization.preview.TestContextSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.coremedia.elastic.social.api.users.CommunityUser.State.ACTIVATED;
import static com.coremedia.personalization.preview.PreviewPersonalizationHandlerInterceptor.QUERY_PARAMETER_TESTCONTEXT;

public class TestUserProfileAutoLoginFilter implements Filter {

  private static Logger LOG = LoggerFactory.getLogger(TestUserProfileAutoLoginFilter.class);

  private static final String PERSONAL_CONTEXT_NAME = "personal";

  private static final String GIVEN_NAME = "givenname";

  @Inject
  private TestContextSource testContextSource;

  @Inject
  private ContextCollection contextCollection;

  @Inject
  private AuthenticationManager authenticationManager;

  @Inject
  private CommunityUserService communityUserService;

  private Authentication beforeTestModeAuthentication;
  private CommunityUser beforeTestModeUser;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    LOG.trace("init");
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    // no need to synchronize, spring social runs before this filter, so its too late anyway
    String parameter = request.getParameter(QUERY_PARAMETER_TESTCONTEXT);

    // if NOT in test mode: get user and authentication. we'll use these to restore WHEN in test mode. this means we need class variables.
    if (parameter == null && request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
      beforeTestModeAuthentication = SecurityContextHolder.getContext().getAuthentication();
      beforeTestModeUser = UserContext.getUser(); // should never return the test profile user! otherwise the test profile logout bug is back.
    }

    // if we're in test mode
    if (parameter != null && request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
      LOG.debug("request parameter {} is set, trying to active elastic social test user", QUERY_PARAMETER_TESTCONTEXT);

      evaluateTestUserProfile((HttpServletRequest) request, (HttpServletResponse) response);

      try {
        chain.doFilter(request, response);
      } finally {
        // finally always log off test profile user

        // if we have a previous logged in user..
        if (beforeTestModeUser != null) {
          // restore original credentials
          LOG.debug("restoring authentication for user {}", beforeTestModeUser);
          SecurityContextHolder.getContext().setAuthentication(beforeTestModeAuthentication);
          UserContext.setUser(beforeTestModeUser);
        } else {
          // only log out test profile user
          if (LOG.isDebugEnabled()) {
            LOG.debug("logging off test user {}", UserContext.getUser());
          }
          UserContext.clear();
          // next, setting authentication null is necessary, to fix the logout bug.
          // or otherwise UserContext.getUser() in the next non-test request will still give the previous test user.
          SecurityContextHolder.getContext().setAuthentication(null);
          SecurityContextHolder.clearContext();
        }
      }
    } else {
      // just ignore request and do nothing
      LOG.debug("request parameter {} is not set, ignoring request", QUERY_PARAMETER_TESTCONTEXT);
      chain.doFilter(request, response);
    }
  }

  /**
   * Reads CUG user information from the test profile and exchanges the current Spring SecurityContext
   * Authentication to test profile credentials, if these are set on the profile.
   */
  private void evaluateTestUserProfile(HttpServletRequest request, HttpServletResponse response) {
    try {
      testContextSource.preHandle(request, response, contextCollection);
      Object personalContext = contextCollection.getContext(PERSONAL_CONTEXT_NAME);
      Object authorities = contextCollection.getContext(CUGAuthorityLoader.AUTHORITIES);

      if (personalContext instanceof PropertyProvider) {
        PropertyProvider personalProperties = (PropertyProvider) personalContext;
        Collection<String> propertyNames = personalProperties.getPropertyNames();
        String givenname = (String) personalProperties.getProperty(GIVEN_NAME);

        if (StringUtils.isNotBlank(givenname)) {

          Map<String, String> properties = new HashMap<>();
          for (String string : propertyNames) {
            Object value = personalProperties.getProperty(string);
            if (value instanceof String) {
              properties.put(string, (String) value);
            } else if (value != null) {
              properties.put(string, value.toString());
            }
          }

          List<CMSymbol> authoritiyGroups = new ArrayList<>();
          if (authorities instanceof PropertyProvider) {
            PropertyProvider authoritiesProperties = (PropertyProvider) authorities;
            Object property = authoritiesProperties.getProperty(CUGAuthorityLoader.AUTHORITIES);
            if (property instanceof List) {
              List<CMSymbol> groups = (List<CMSymbol>) property;
              if (!groups.isEmpty()) {
                authoritiyGroups = groups;
              }
            }
          }
          // now use the information to exchange credentials
          CommunityUser communityUser = getOrCreateCommunityUser(properties, authoritiyGroups);
          Authentication authenticationToken = new SocialAuthenticationToken(communityUser.getName(), givenname);
          Authentication authentication = authenticationManager.authenticate(authenticationToken);
          SecurityContext securityContext = SecurityContextHolder.getContext();
          securityContext.setAuthentication(authentication);
          UserContext.setUser(communityUser);
          // done with exchanging user credentials
        }
      } else {
        LOG.trace("cannot handle securityContext {}", personalContext);
      }
    } catch (Exception e) {
      LOG.warn("test securityContext source threw exception: {}", e.getMessage());
    }
  }

  private CommunityUser getOrCreateCommunityUser(Map<String, String> userProperties, List<CMSymbol> authorityGroups) {
    String username = userProperties.get(GIVEN_NAME);
    CommunityUser communityUser = communityUserService.getUserByName(username);

    HashMap<String, Object> properties = new HashMap<String, Object>(userProperties);
    properties.put(CUGAuthorityLoader.AUTHORITIES, authorityGroups);

    if (communityUser == null) {
      String email = userProperties.get("email");
      try {
        communityUser = communityUserService.createUser(username, userProperties.get(GIVEN_NAME), email);
        communityUser.setProperty("state", ACTIVATED);
        communityUser.setProperties(properties);
        communityUser.setLocale(Locale.getDefault());
        communityUser.save();
        LOG.debug("Created model with name {}: {}", username, communityUser);
      } catch (DuplicateEmailException e) {
        LOG.warn("User with duplicate email {}", e.getEmail());
      } catch (DuplicateNameException e) {
        LOG.warn("User with duplicate name {}", e.getName());
      }
    } else {
      communityUser.setProperties(properties);
      communityUser.save();
      LOG.debug("User with name {} already exists", username);
    }
    return communityUserService.createFrom(communityUser);
  }


  @Override
  public void destroy() {
    LOG.trace("destroy");
  }
}
