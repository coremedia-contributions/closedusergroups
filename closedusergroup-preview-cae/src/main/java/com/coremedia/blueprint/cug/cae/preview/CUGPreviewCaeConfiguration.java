package com.coremedia.blueprint.cug.cae.preview;

import com.coremedia.springframework.customizer.Customize;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;

@Configuration
@ImportResource(reader = ResourceAwareXmlBeanDefinitionReader.class,
        value = {
                "classpath:/com/coremedia/cae/contentbean-services.xml",
                "classpath:/META-INF/coremedia/p13n-preview-cae-context.xml"
        }
)
public class CUGPreviewCaeConfiguration {
  @Bean
  public TestUserProfileAutoLoginFilter testUserProfileAutoLoginFilter() {
    return new TestUserProfileAutoLoginFilter();
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
