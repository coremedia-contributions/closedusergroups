package com.coremedia.blueprint.cug.filter;

import com.coremedia.caas.headless_server.plugin_support.extensionpoints.FilterPredicate;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.collector.ContextCollectionConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        ContextCollectionConfiguration.class
})
public class HeadlessServerConfig {


  @Bean
  @Qualifier("filterPredicate")
  public FilterPredicate<Object> authoritiesPredicate(ContextCollection contextCollection) {
    return new AuthoritiesPredicate(contextCollection);
  }
}
