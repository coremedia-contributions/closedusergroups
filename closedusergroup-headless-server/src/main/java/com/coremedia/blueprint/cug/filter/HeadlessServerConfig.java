package com.coremedia.blueprint.cug.filter;

import com.coremedia.caas.headless_server.plugin_support.extensionpoints.FilterPredicate;
import com.coremedia.cap.undoc.common.spring.CapRepositoriesConfiguration;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.collector.ContextCollectionConfiguration;
import com.coremedia.personalization.context.collector.ContextCollectorConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

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