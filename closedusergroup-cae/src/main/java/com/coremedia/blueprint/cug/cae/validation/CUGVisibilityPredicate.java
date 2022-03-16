package com.coremedia.blueprint.cug.cae.validation;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.blueprint.cug.CUGAuthorityStrategy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * This predicate is used to filter {@link CMLinkable} by authorities of the current user.
 * <p/>
 * It checks as well, if the content bean should still be teasered to certain authorities.
 * Then it will not be filtered here, so it can be used for teaser views.
 * Therefore further access checks on the controller or view level are required.
 * <p/>
 * Put differently, this filter applies, if the content bean really should never be seen
 * by the current user according to her granted authorities.
 */
public class CUGVisibilityPredicate implements Predicate<CMLinkable> {

  private final CUGAuthorityStrategy cugAuthorityStrategy;
  private final ContextHelper contextHelper;

  public CUGVisibilityPredicate(CUGAuthorityStrategy cugAuthorityStrategy, ContextHelper contextHelper) {
    this.cugAuthorityStrategy = cugAuthorityStrategy;
    this.contextHelper = contextHelper;
  }

  public boolean test(CMLinkable securable) {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();

    // null checks
    if (securable == null || authentication == null || !authentication.isAuthenticated()) {
      return false; // invalid
    }

    List<GrantedAuthority> accessAllowedAuthorities = cugAuthorityStrategy.getAccessAllowedAuthorities(securable.getContent());
    CMNavigation contextFor = contextHelper.contextFor(securable);
    if (contextFor != null) {
      accessAllowedAuthorities.addAll(cugAuthorityStrategy.getAccessAllowedAuthorities(contextFor.getContent()));

      if (!accessAllowedAuthorities.isEmpty()) {
        Collection<? extends GrantedAuthority> userAuthorities = authentication.getAuthorities();

        // match user authorities to 'accessAllowed'
        return !Collections.disjoint(userAuthorities, accessAllowedAuthorities);
      }

      return true; // valid because of no restrictions
    }
    else {
      return true;
    }
  }
}
