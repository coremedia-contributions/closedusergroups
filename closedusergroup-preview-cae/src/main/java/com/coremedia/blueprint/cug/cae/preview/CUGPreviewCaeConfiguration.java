package com.coremedia.blueprint.cug.cae.preview;

import com.coremedia.blueprint.personalization.preview.config.P13NPreviewCaeContextConfiguration;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.collector.ContextCollectionConfiguration;
import com.coremedia.personalization.preview.TestContextSource;
import com.coremedia.springframework.customizer.Customize;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;

@AutoConfiguration
@Import({
        P13NPreviewCaeContextConfiguration.class,
        ContextCollectionConfiguration.class
})
@ImportResource(reader = ResourceAwareXmlBeanDefinitionReader.class,
        value = {
                "classpath:/com/coremedia/cae/contentbean-services.xml",
        }
)
public class CUGPreviewCaeConfiguration {
  @Bean
  public TestUserProfileAutoLoginFilter testUserProfileAutoLoginFilter(ContextCollection contextCollection, TestContextSource testContextSource, AuthenticationManager authenticationManager) {
    return new TestUserProfileAutoLoginFilter(contextCollection, testContextSource, authenticationManager);
  }


  @Bean
  @Customize("testContextExtractors")
  @Order(20002)
  public AuthorityGroupsExtractor addCUGTestContextExtractors() {
    return new AuthorityGroupsExtractor();
  }

  @Bean
  public CUGCaeFilters cugCaeFilters() {
    return new CUGCaeFilters();
  }
}
