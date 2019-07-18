package com.revengemission.sso.oauth2.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 部分接口需要跨域支持
 */
@Configuration
public class CustomCorsConfiguration {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("GET");
        corsConfiguration.addAllowedMethod("POST");
        corsConfiguration.addAllowedMethod("OPTIONS");
///        corsConfiguration.addExposedHeader("head1");
///        corsConfiguration.addExposedHeader("Location");
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/oauth/**", corsConfiguration);
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/user/me", corsConfiguration);
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/captcha/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
