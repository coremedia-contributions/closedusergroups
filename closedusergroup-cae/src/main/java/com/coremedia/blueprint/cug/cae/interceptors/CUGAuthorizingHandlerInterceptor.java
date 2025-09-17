package com.coremedia.blueprint.cug.cae.interceptors;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.contentbeans.PageImpl;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.cug.CUGAuthorityStrategy;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.coremedia.objectserver.web.HandlerHelper;
import jakarta.inject.Inject;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CUGAuthorizingHandlerInterceptor implements HandlerInterceptor {

  private SettingsService settingsService;
  private CUGAuthorityStrategy cugAuthorityStrategy;
  private DataViewFactory dataViewFactory;

  public CUGAuthorizingHandlerInterceptor(CUGAuthorityStrategy cugAuthorityStrategy, SettingsService settingsService, DataViewFactory dataViewFactory) {
    this.settingsService = settingsService;
    this.cugAuthorityStrategy = cugAuthorityStrategy;
    this.dataViewFactory = dataViewFactory;
  }

  @Inject
  private BeanFactory beanFactory;

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    /* Because we need content beans to do authorization checks, we can do this only in #postHandle.
     * We'll check the ModelAndView for its content bean and navigation. If the request targets an unauthorized
     * content or navigation, we'll send the user/the request somewhere else authorized.
     */
    if (modelAndView != null) {
      Object rootModel = HandlerHelper.getRootModel(modelAndView);

      // get authentication information
      SecurityContext securityContext = SecurityContextHolder.getContext();
      Authentication authentication = securityContext.getAuthentication();

      if (rootModel instanceof Page) {
        Page page = (Page) rootModel;

        Navigation navigation = page.getNavigation();
        Object content = page.getContent();


        // authorization checks
        if (navigation instanceof CMLinkable) {
          if (authorizationChecks(((CMLinkable) navigation), authentication)) {
            doRedirect(modelAndView, navigation);
            return;
          }
        }
        if (content instanceof CMLinkable) {
          if (authorizationChecks(((CMLinkable) content), authentication)) {
            doRedirect(modelAndView, navigation);
          }
        }
      } else if (rootModel instanceof CMChannel) {
        CMChannel channel = (CMChannel) rootModel;
        if (authorizationChecks(channel, authentication)) {
          doRedirect(modelAndView, channel);
        }
      }
    }
  }

  private boolean authorizationChecks(CMLinkable cmLinkable, Authentication authentication) {
    boolean channelIsAuthorized;
    List<GrantedAuthority> accessAllowedAuthorities = cugAuthorityStrategy.getAccessAllowedAuthorities(cmLinkable.getContent());
    if (!accessAllowedAuthorities.isEmpty()) {
      channelIsAuthorized = !Collections.disjoint(authentication.getAuthorities(), accessAllowedAuthorities);
      return !channelIsAuthorized;
    }
    return false;
  }

  /**
   * Processes the mav in order to redirect away from the unauthorized content.
   *
   * @param modelAndView      the original passed mav we need to alter
   * @param currentNavigation needed for redirect calculation. since we already extracted this from the mav above,
   *                          just pass it as well instead of recomputing it from the mav.
   */
  private void doRedirect(ModelAndView modelAndView, Navigation currentNavigation) {
    // TODO: we definitely want to display a certain view or message to the user.
    // instead of the current logic, we could set a desired redirect content bean (with corresponding content type and view)
    // obtained from the channel. depending on the current user authentication we could render something like
    // "please upgrade your subscription" or "please login first to access subscribed content" etc.
    // in order to let the user know, that access to the premium content is not authorized for a certain reason.

    // we'll redirect to this page
    Page redirectPage;

    // simple solution: get setting "cug-conversion-channel" from the root channel, which links to the to-be-shown channel
    Navigation rootNavigation = currentNavigation.getRootNavigation();

    CMChannel cugSettingConversionChannel = settingsService.setting("cug-conversion-channel", CMChannel.class, rootNavigation);
    redirectPage = asPage(Objects.requireNonNullElse(cugSettingConversionChannel, currentNavigation));

    // ModelAndView manipulation
    modelAndView.getModelMap().addAttribute("self", redirectPage);
    modelAndView.getModelMap().addAttribute("cmpage", redirectPage);
    // keep the rest
  }

  private Page asPage(Navigation context) {
    PageImpl page = beanFactory.getBean("cmPage", PageImpl.class);
    page.setContent(context);
    page.setNavigation(context);
    page.setTitle(context.getTitle());
    page.setDescription(context.getTitle());
    page.setKeywords(context.getKeywords());
    if (context instanceof CMLinkable) {
      CMLinkable cmLinkable = (CMLinkable) context;
      page.setContentId(String.valueOf(cmLinkable.getContentId()));
      page.setContentType(cmLinkable.getContent().getType().getName());
      page.setValidFrom(cmLinkable.getValidFrom());
      page.setValidTo(cmLinkable.getValidTo());
    }
    // load a dataview for the page
    if (dataViewFactory != null) {
      page = dataViewFactory.loadCached(page, null);
    }
    return page;
  }
}
