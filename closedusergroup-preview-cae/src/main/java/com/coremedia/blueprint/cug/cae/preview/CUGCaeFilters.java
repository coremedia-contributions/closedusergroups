package com.coremedia.blueprint.cug.cae.preview;

import com.coremedia.springframework.boot.web.servlet.RegistrationBeanBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class CUGCaeFilters {
  private static final String SERVLET = "/servlet/*";

  // site filter is registered in com.coremedia.blueprint.component.cae.CaeBaseComponentConfiguration with order 100
  private static final int ORDER_SITE_FILTER = 100;

  @Bean
  public FilterRegistrationBean testUserProfileAutoLoginFilterRegistration(Filter testUserProfileAutoLoginFilter) {
    return RegistrationBeanBuilder
            .forFilter(testUserProfileAutoLoginFilter)
            .urlPatterns(SERVLET)
            .order(ORDER_SITE_FILTER + 40)
            .build();
  }

}
