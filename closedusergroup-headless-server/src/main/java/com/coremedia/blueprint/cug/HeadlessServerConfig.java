package com.coremedia.blueprint.cug;

import com.coremedia.blueprint.caas.customization.configuration.HeadlessServerCustomizationsAutoConfiguration;
import com.coremedia.blueprint.caas.customization.preview.CommerceSegmentTestContextExtractor;
import com.coremedia.blueprint.caas.customization.preview.DateTimeTestContextSource;
import com.coremedia.blueprint.caas.customization.preview.PropertyBasedTestContextExtractor;
import com.coremedia.blueprint.caas.customization.preview.TestContextSource;
import com.coremedia.blueprint.cug.filter.AuthoritiesPredicate;
import com.coremedia.blueprint.cug.filter.AuthoritiesTestContextExtractor;
import com.coremedia.caas.headless_server.plugin_support.extensionpoints.FilterPredicate;
import com.coremedia.id.IdScheme;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.collector.ContextCollectionConfiguration;
import com.coremedia.personalization.context.collector.ContextCollector;
import com.coremedia.personalization.context.collector.ContextSource;
import com.coremedia.personalization.context.collector.PersonalizationHandlerInterceptor;
import com.coremedia.personalization.preview.PreviewPersonalizationHandlerInterceptor;
import com.coremedia.personalization.preview.PropertiesTestContextExtractor;
import com.coremedia.personalization.preview.TestContextExtractor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Map;

@AutoConfiguration(after = HeadlessServerCustomizationsAutoConfiguration.class)
@Import({
        ContextCollectionConfiguration.class
})
public class HeadlessServerConfig {


  @Bean
  @Qualifier("filterPredicate")
  public FilterPredicate<Object> authoritiesPredicate(ContextCollection contextCollection, CUGAuthorityStrategy closedUserGroupGatedContentStrategy) {
    return new AuthoritiesPredicate(contextCollection, closedUserGroupGatedContentStrategy);
  }

  @Bean
  public ContextSource authoritiesContextSource(IdScheme contentIdScheme, List<? extends TestContextExtractor> authoritiesContextExtractors) {
    return new TestContextSource(contentIdScheme, authoritiesContextExtractors);
  }

  @Bean
  public List<? extends TestContextExtractor> authoritiesContextExtractors() {
    return List.of(new AuthoritiesTestContextExtractor());
  }

  @Bean
  public HandlerInterceptor authoritiesContextCollectorInterceptor(ContextCollector contextCollector, ContextSource authoritiesContextSource) {
    PreviewPersonalizationHandlerInterceptor testContextCollectorInterceptor = new PreviewPersonalizationHandlerInterceptor();

    PersonalizationHandlerInterceptor withOutTestContext = new PersonalizationHandlerInterceptor();
    contextCollector.addContextSource(authoritiesContextSource);
    withOutTestContext.setContextCollector(contextCollector);

    PersonalizationHandlerInterceptor withTestContext = new PersonalizationHandlerInterceptor();
    withTestContext.setContextCollector(contextCollector);


    testContextCollectorInterceptor.setWithTestContext(withTestContext);
    testContextCollectorInterceptor.setWithoutTestContext(withOutTestContext);
    return testContextCollectorInterceptor;
  }

}
