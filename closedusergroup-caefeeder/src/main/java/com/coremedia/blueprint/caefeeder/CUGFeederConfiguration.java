package com.coremedia.blueprint.caefeeder;

import com.coremedia.blueprint.cug.CUGAuthorityStrategy;
import com.coremedia.blueprint.cug.CUGConfiguration;
import com.coremedia.springframework.customizer.Customize;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@AutoConfiguration
@Import(
        value = {
                CUGConfiguration.class
        }
)
@ImportResource(reader = ResourceAwareXmlBeanDefinitionReader.class,
        value = {
                "classpath:/framework/spring/caefeeder/caefeeder-triggers.xml"
        }
)
public class CUGFeederConfiguration {

  @Bean
  @Customize("feedablePopulators")
  public RequiredRolesFeedablePopulator cugFeedablePopulatorsCustomizer(@NonNull CUGAuthorityStrategy closedUserGroupGatedContentStrategy) {
    return new RequiredRolesFeedablePopulator(closedUserGroupGatedContentStrategy);
  }
}

