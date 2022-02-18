package com.coremedia.blueprint.cug.search;

import com.coremedia.blueprint.cae.search.SearchFilterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

public class CUGSolrFilterProviderImpl implements SearchFilterProvider<String> {

  private static final Logger LOG = LoggerFactory.getLogger(CUGSolrFilterProviderImpl.class);

  @Override
  public List<String> getFilter(boolean isPreview) {

    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    List<String> customVerbatimFilters = new ArrayList<>();

    /*
     * requiredRoles:ROLE_GOLD OR requiredRoles:ROLE_SILVER                       finds only gold and silver content
     * -requiredRoles:*                                                           finds public content without any roles set
     * requiredRoles:ROLE_GOLD OR requiredRoles:ROLE_SILVER OR -requiredRoles:*   finds NOTHING. /!\
     *
     * but there is a workaround:
     *
     * (requiredRoles:ROLE_SILVER OR requiredRoles:ROLE_GOLD) OR (-requiredRoles:* AND *:*)   actually finds everything.
     * (requiredRoles:ROLE_SILVER) OR (-requiredRoles:* AND *:*)                              finds everything BUT gold content. (this is what we need.)
     *
     */

    StringBuilder authorizedContentCondition = new StringBuilder();

    for (GrantedAuthority authority : authentication.getAuthorities()) {
      authorizedContentCondition.append("requiredRoles:")
              .append(authority.getAuthority())
              .append(" OR ");
    }
    authorizedContentCondition.append("(-requiredRoles:* AND *:*)");
    customVerbatimFilters.add(authorizedContentCondition.toString());
    LOG.debug("Added custom Solr filter query: " + authorizedContentCondition);
    return customVerbatimFilters;
  }
}
