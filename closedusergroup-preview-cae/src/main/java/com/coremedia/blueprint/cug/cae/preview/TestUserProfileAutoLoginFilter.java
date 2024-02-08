package com.coremedia.blueprint.cug.cae.preview;

import com.coremedia.blueprint.cug.cae.CUGAuthenticationToken;
import com.coremedia.cap.content.Content;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.PropertyProvider;
import com.coremedia.personalization.preview.TestContextSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.coremedia.blueprint.cug.CUGAuthorityStrategy.MAPPER;
import static com.coremedia.personalization.preview.PreviewPersonalizationHandlerInterceptor.QUERY_PARAMETER_TESTCONTEXT;

public class TestUserProfileAutoLoginFilter implements Filter {

  private static final Logger LOG = LoggerFactory.getLogger(TestUserProfileAutoLoginFilter.class);

  private static final String PERSONAL_CONTEXT_NAME = "personal";

  private static final String GIVEN_NAME = "givenname";

  private final TestContextSource testContextSource;

  private final ContextCollection contextCollection;

  private final AuthenticationManager authenticationManager;

  public TestUserProfileAutoLoginFilter(ContextCollection contextCollection, TestContextSource testContextSource, AuthenticationManager authenticationManager) {
    this.contextCollection = contextCollection;
    this.testContextSource = testContextSource;
    this.authenticationManager = authenticationManager;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    // no need to synchronize, spring social runs before this filter, so its too late anyway
    String parameter = request.getParameter(QUERY_PARAMETER_TESTCONTEXT);
    // if we're in test mode
    if (parameter != null && request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
      LOG.debug("request parameter {} is set, trying to active elastic social test user", QUERY_PARAMETER_TESTCONTEXT);
      evaluateTestUserProfile((HttpServletRequest) request, (HttpServletResponse) response);


      try {
        chain.doFilter(request, response);
      } finally {
        SecurityContextHolder.getContext().setAuthentication(null);
        SecurityContextHolder.clearContext();
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
      Object authorities = contextCollection.getContext(AuthorityGroupsExtractor.AUTHORITIES);

      if (personalContext instanceof PropertyProvider) {
        String givenname = (String) ((PropertyProvider) personalContext).getProperty(GIVEN_NAME);

        if (StringUtils.isNotBlank(givenname)) {

          List<Content> authoritiyGroups = new ArrayList<>();
          if (authorities instanceof PropertyProvider) {
            PropertyProvider authoritiesProperties = (PropertyProvider) authorities;
            Object property = authoritiesProperties.getProperty(AuthorityGroupsExtractor.AUTHORITIES);
            if (property instanceof List) {
              List<Content> groups = (List<Content>) property;
              if (!groups.isEmpty()) {
                authoritiyGroups = groups;
              }
            }
          }
          // now use the information to exchange credentials
          List<String> collect = authoritiyGroups.stream().map(item -> item.getName().toUpperCase(Locale.ROOT)).collect(Collectors.toList());
          List<GrantedAuthority> grantedAuthorities = MAPPER.getGrantedAuthorities(collect);
          Authentication authenticationToken = new CUGAuthenticationToken(givenname, givenname, grantedAuthorities);
          Authentication authentication = authenticationManager.authenticate(authenticationToken);
          SecurityContext securityContext = SecurityContextHolder.getContext();
          securityContext.setAuthentication(authentication);
          // done with exchanging user credentials
        }
      } else {
        LOG.trace("cannot handle securityContext {}", personalContext);
      }
    } catch (Exception e) {
      LOG.warn("test securityContext source threw exception: {}", e.getMessage());
    }
  }
}
