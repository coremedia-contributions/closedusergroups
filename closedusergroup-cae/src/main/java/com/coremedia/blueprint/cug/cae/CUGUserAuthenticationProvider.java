package com.coremedia.blueprint.cug.cae;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;


public class CUGUserAuthenticationProvider implements AuthenticationProvider {


  @Override
  public Authentication authenticate(Authentication authentication) {
    if (authentication instanceof CUGAuthenticationToken) {
      CUGAuthenticationToken authenticationToken = (CUGAuthenticationToken) authentication;
      return new UsernamePasswordAuthenticationToken(
              authenticationToken.getName(),
              "", authenticationToken.getAuthorities());
    }

    throw new IllegalArgumentException("Authentication type " + authentication.getClass() + " is not supported.");
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return CUGAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
