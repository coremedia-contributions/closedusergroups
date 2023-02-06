package com.coremedia.blueprint.cug;

import com.coremedia.blueprint.base.folderproperties.FolderPropertiesFinder;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAttributes2GrantedAuthoritiesMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CUGAuthorityStrategy {

  public static final SimpleAttributes2GrantedAuthoritiesMapper MAPPER = new SimpleAttributes2GrantedAuthoritiesMapper();
  private final FolderPropertiesFinder folderPropertiesFinder;

  public CUGAuthorityStrategy(FolderPropertiesFinder folderPropertiesFinder) {
    this.folderPropertiesFinder = folderPropertiesFinder;
  }

  public List<GrantedAuthority> getAccessAllowedAuthorities(Content content) {
    List<String> accessAllowedAuthorities = new ArrayList<>();
    Content folderConfig = folderPropertiesFinder.lookupFolderProperties(content.getParent());
    if (folderConfig != null && folderConfig.getProperties().containsKey("localSettings")) {
      Struct localSettings = folderConfig.getStruct("localSettings");
      if (localSettings != null && localSettings.getType().getDescriptor("accessAllowedAuthorities") != null) {
        List<Content> accessAllowedAuthoritiesSymbols = localSettings.getLinks("accessAllowedAuthorities");
        for (Content accessAllowedAuthoritiesSymbol : accessAllowedAuthoritiesSymbols) {
          accessAllowedAuthorities.add(accessAllowedAuthoritiesSymbol.getName().toUpperCase(Locale.getDefault()));
        }
      }
      // if no authority found and not yet in the root folder, try further up the folder hierarchy
      if (accessAllowedAuthorities.isEmpty() && (folderConfig.getParent() != null && !folderConfig.getParent().isRoot())) {
        return getAccessAllowedAuthorities(folderConfig.getParent());
      }
    }
    return MAPPER.getGrantedAuthorities(accessAllowedAuthorities);
  }
}
