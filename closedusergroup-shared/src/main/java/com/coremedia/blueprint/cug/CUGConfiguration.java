package com.coremedia.blueprint.cug;

import com.coremedia.blueprint.base.navigation.context.finder.impl.FolderPropertiesEvaluatingContextFinder;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

@AutoConfiguration
@ImportResource(reader = ResourceAwareXmlBeanDefinitionReader.class,
        value = {
                "classpath:/com/coremedia/blueprint/base/navigation/context/finder/bpbase-context-finder-services.xml"
        }
)
public class CUGConfiguration {

  @Bean
  public CUGAuthorityStrategy closedUserGroupGatedContentStrategy(FolderPropertiesEvaluatingContextFinder folderPropertiesEvaluatingContextFinder) {
    return new CUGAuthorityStrategy(folderPropertiesEvaluatingContextFinder);
  }
}
