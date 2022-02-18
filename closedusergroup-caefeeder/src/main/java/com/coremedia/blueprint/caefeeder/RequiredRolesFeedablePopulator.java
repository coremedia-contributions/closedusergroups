package com.coremedia.blueprint.caefeeder;

import com.coremedia.blueprint.cug.CUGAuthorityStrategy;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.feeder.MutableFeedable;
import com.coremedia.cap.feeder.populate.FeedablePopulator;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Returns the required authorities as a String in the form:
 * <p/>
 * "ROLE_X,ROLE_Y,ROLE_Z".
 * <p/>
 */
public class RequiredRolesFeedablePopulator implements FeedablePopulator<Content> {

  private final CUGAuthorityStrategy cugAuthorityStrategy;

  public RequiredRolesFeedablePopulator(CUGAuthorityStrategy cugAuthorityStrategy) {
    this.cugAuthorityStrategy = cugAuthorityStrategy;
  }

  @Override
  public void populate(MutableFeedable feedable, Content source) {
    if (feedable == null || source == null) {
      throw new IllegalArgumentException("feedable and source must not be null");
    }
    List<GrantedAuthority> accessAllowedAuthorities = cugAuthorityStrategy.getAccessAllowedAuthorities(source);
    // skip empty values and duplicates and combine into comma-separated result string
    String result = accessAllowedAuthorities.stream()
            .map(GrantedAuthority::getAuthority)
            .filter(authority -> authority != null && !authority.trim().isEmpty())
            .distinct()
            .collect(Collectors.joining(","));
    if (!result.isEmpty()) {
      feedable.setElement("requiredRoles", result);
    }
  }
}
