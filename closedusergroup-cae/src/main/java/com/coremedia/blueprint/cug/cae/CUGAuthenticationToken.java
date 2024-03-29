package com.coremedia.blueprint.cug.cae;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CUGAuthenticationToken extends UsernamePasswordAuthenticationToken {
  public CUGAuthenticationToken( Object principal,  Object credentials,
                                 Collection<? extends GrantedAuthority> authorities) {
    super(principal, credentials, authorities);
  }
}
