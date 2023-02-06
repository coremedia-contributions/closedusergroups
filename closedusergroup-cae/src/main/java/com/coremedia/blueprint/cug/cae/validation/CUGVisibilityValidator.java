package com.coremedia.blueprint.cug.cae.validation;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.blueprint.common.services.validation.AbstractValidator;
import com.coremedia.blueprint.cug.CUGAuthorityStrategy;

import java.util.function.Predicate;

/**
 * This validation provider checks {@link CMLinkable} for its Spring Security Authorities and considers securable only valid,
 * if current user is authorized by its {@link org.springframework.security.core.GrantedAuthority} or the securable has no authorities.
 */
public class CUGVisibilityValidator extends AbstractValidator<CMLinkable> {

  private final CUGAuthorityStrategy cugAuthorityStrategy;
  private final ContextHelper contextHelper;

  public CUGVisibilityValidator(CUGAuthorityStrategy cugAuthorityStrategy, ContextHelper contextHelper) {
    this.cugAuthorityStrategy = cugAuthorityStrategy;
    this.contextHelper = contextHelper;
  }

  public boolean supports(Class<?> clazz) {
    return CMLinkable.class.isAssignableFrom(clazz);
  }

  protected Predicate<CMLinkable> createPredicate() {
    return new CUGVisibilityPredicate(cugAuthorityStrategy, contextHelper);
  }

}
