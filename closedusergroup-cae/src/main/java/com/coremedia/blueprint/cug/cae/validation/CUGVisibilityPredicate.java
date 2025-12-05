package com.coremedia.blueprint.cug.cae.validation;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.blueprint.cug.CUGAuthorityStrategy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
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

  private final CUGAuthorityStrategy authorityStrategy;
  private final ContextHelper contextHelper;

  public CUGVisibilityPredicate(CUGAuthorityStrategy authorityStrategy, ContextHelper contextHelper) {
    this.authorityStrategy = authorityStrategy;
    this.contextHelper = contextHelper;
  }

  @Override
  public boolean test(CMLinkable bean) {
    if (bean == null) {
      return false;
    }

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return false;
    }

    // Collect allowed authorities from bean + its navigation context
    List<GrantedAuthority> allowed = authorityStrategy.getAccessAllowedAuthorities(bean.getContent());

    CMNavigation nav = contextHelper.contextFor(bean);
    if (nav != null) {
      allowed.addAll(authorityStrategy.getAccessAllowedAuthorities(nav.getContent()));
    }

    // If no restrictions → allow
    if (allowed.isEmpty()) {
      return true;
    }

    // If any user authority appears in allowed → allow
    Collection<? extends GrantedAuthority> userAuthorities = auth.getAuthorities();
    return userAuthorities.stream().anyMatch(allowed::contains);
  }
}
