package com.coremedia.blueprint.cug.filter;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.PropertyProfile;
import com.coremedia.personalization.preview.TestContextExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public final class AuthoritiesTestContextExtractor implements TestContextExtractor {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthoritiesTestContextExtractor.class);


  @Override
  public void extractTestContextsFromContent(final Content content, final ContextCollection contextCollection) {
    if (content == null || contextCollection == null) {
      LOGGER.debug("supplied content or contextCollection are null; cannot extract any contexts");
      return;
    }

    if (!(content.getType().isSubtypeOf("CMUserProfile"))) {
      return;
    }

      extractTestContexts(content, contextCollection);
  }

  private void extractTestContexts(Content userProfile, ContextCollection contextCollection) {
    final PropertyProfile propertyProfile = new PropertyProfile();
    Struct profileExtensions = userProfile.getStruct("profileExtensions");
    if (profileExtensions != null) {
      Object value = profileExtensions.get("properties");
      if(value instanceof Struct && ((Struct) value).get("authorities") != null) {
        List<Content> authorities = ((Struct) value).getLinks("authorities");

        authorities.stream()
                .filter(Objects::nonNull)
                .map(Content.class::cast)
                .map(Content::getName).forEach(item -> propertyProfile.setProperty("ROLE_"+item.toUpperCase(), 1));
        contextCollection.setContext("authorities", propertyProfile);
      }
    }
  }
}
