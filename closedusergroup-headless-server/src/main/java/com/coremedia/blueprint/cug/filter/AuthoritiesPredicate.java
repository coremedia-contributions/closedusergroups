package com.coremedia.blueprint.cug.filter;

import com.coremedia.caas.headless_server.plugin_support.extensionpoints.FilterPredicate;
import com.coremedia.personalization.context.ContextCollection;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthoritiesPredicate implements FilterPredicate<Object> {
  private static final Logger LOG = LoggerFactory.getLogger(AuthoritiesPredicate.class);
  private final ContextCollection contextCollection;

  public AuthoritiesPredicate(ContextCollection contextCollection) {
    this.contextCollection = contextCollection;
  }

  @Override
  public boolean test(DataFetchingEnvironment dataFetchingEnvironment, Object argument) {
    boolean result = true;
    LOG.debug("Authorities filter loaded");

    return result;
  }

}
