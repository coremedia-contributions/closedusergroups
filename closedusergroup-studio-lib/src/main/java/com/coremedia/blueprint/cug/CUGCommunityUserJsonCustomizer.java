package com.coremedia.blueprint.cug;

import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.rest.api.JsonCustomizer;

import javax.inject.Named;
import java.util.List;
import java.util.Map;

@Named
public class CUGCommunityUserJsonCustomizer implements JsonCustomizer<CommunityUser> {
  public void customize(CommunityUser communityUser, Map<String, Object> serializedObject) {
    //keep in sync with "authorities"
    serializedObject.put("authorities", communityUser.getProperty("authorities", List.class));
  }
}
