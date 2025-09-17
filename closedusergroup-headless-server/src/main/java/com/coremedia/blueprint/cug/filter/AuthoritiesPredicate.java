package com.coremedia.blueprint.cug.filter;

import com.coremedia.blueprint.cug.CUGAuthorityStrategy;
import com.coremedia.caas.headless_server.plugin_support.extensionpoints.FilterPredicate;
import com.coremedia.cap.content.Content;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.PropertyProvider;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public class AuthoritiesPredicate implements FilterPredicate<Object> {
  private static final Logger LOG = LoggerFactory.getLogger(AuthoritiesPredicate.class);
  private final ContextCollection contextCollection;
  private final CUGAuthorityStrategy closedUserGroupGatedContentStrategy;

  public AuthoritiesPredicate(ContextCollection contextCollection, CUGAuthorityStrategy closedUserGroupGatedContentStrategy) {
    this.contextCollection = contextCollection;
    this.closedUserGroupGatedContentStrategy = closedUserGroupGatedContentStrategy;
  }

  @Override
  public boolean test(DataFetchingEnvironment dataFetchingEnvironment, Object o) {
    return !(o instanceof Content) || this.validate(dataFetchingEnvironment, (Content)o);
  }

  private boolean validate(DataFetchingEnvironment dataFetchingEnvironment, Content content) {
    List<GrantedAuthority> allowedAuthorities = closedUserGroupGatedContentStrategy.getAccessAllowedAuthorities(content);
    Object currentAuthorities = contextCollection.getContext("authorities");

    // if no restrictions defined, allow content
    if (allowedAuthorities.isEmpty()) return true;

    if (currentAuthorities instanceof PropertyProvider) {
      Collection<String> propertyNames = ((PropertyProvider) currentAuthorities).getPropertyNames();
      return  allowedAuthorities.stream().anyMatch(authority -> propertyNames.contains(authority.getAuthority()));
    }

    return false;
  }

}
