package com.coremedia.blueprint.cug.cae;

import com.coremedia.blueprint.cug.CUGAuthorityStrategy;
import com.coremedia.cap.content.Content;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.springsecurity.AuthorityLoader;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class CUGAuthorityLoader implements AuthorityLoader {

  public static final String AUTHORITIES = "authorities";

  public Collection<GrantedAuthority> loadAuthorities(CommunityUser communityUser) {
    List<String> result = new ArrayList<>();
    List<Content> authorities = communityUser.getProperty(AUTHORITIES, List.class);
    if (authorities != null && !authorities.isEmpty()) {
      authorities.stream().filter(Content::isInProduction).forEach(authority -> result.add(authority.getName().toUpperCase(Locale.getDefault())));
    }
    return CUGAuthorityStrategy.MAPPER.getGrantedAuthorities(result);
  }
}
