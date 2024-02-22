package com.coremedia.blueprint.cug;

import com.coremedia.blueprint.base.navigation.context.finder.impl.FolderPropertiesEvaluatingContextFinder;
import com.coremedia.blueprint.base.navigation.context.finder.impl.NavigationContextFinderConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
        NavigationContextFinderConfiguration.class
})
public class CUGConfiguration {

  @Bean
  public CUGAuthorityStrategy closedUserGroupGatedContentStrategy(FolderPropertiesEvaluatingContextFinder folderPropertiesEvaluatingContextFinder) {
    return new CUGAuthorityStrategy(folderPropertiesEvaluatingContextFinder);
  }
}
