package com.coremedia.blueprint.cug.cae.preview;

import com.coremedia.cap.content.Content;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.PropertyProfile;
import com.coremedia.personalization.preview.TestContextExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * This test context extractor extracts authority groups  from CMUserProfile's profileExtension property.
 */
public final class AuthorityGroupsExtractor implements TestContextExtractor {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthorityGroupsExtractor.class);
  public static final String AUTHORITIES = "authorities";

  public void extractTestContextsFromContent(final Content content, final ContextCollection contextCollection) {
    if (content == null || contextCollection == null) {
      LOGGER.debug("supplied content or contextCollection are null; cannot extract any contexts");
      return;
    }

    if (content.getProperties().containsKey("profileExtensions")) {
      Map<String, Object> profileExtensions = content.getStruct("profileExtensions").toNestedMaps();
      if (profileExtensions != null && profileExtensions.containsKey("properties")) {
        Object properties = profileExtensions.get("properties");
        if (properties instanceof Map) {
          Map<String, Object> propertiesMap = (Map<String, Object>) properties;
          if (propertiesMap.containsKey(AUTHORITIES)) {
            Object authorities = propertiesMap.get(AUTHORITIES);
            if (authorities instanceof List) {
              PropertyProfile propertyProfile = new PropertyProfile();
              propertyProfile.setProperty(AUTHORITIES, authorities);
              contextCollection.setContext(AUTHORITIES, propertyProfile);
            }
          }
        }
      }
    }
  }
}
