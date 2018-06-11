package com.revengemission.sso.oauth2.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                // Since we want the protected resources to be accessible in the UI as well we need
                // session creation to be allowed (it's disabled by default in 2.0.6)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .requestMatchers().antMatchers("/photos/**", "/user/me")
                .and()
                .authorizeRequests()
                .antMatchers("/user/me").access("#oauth2.hasScope('read')")
                .antMatchers("/photos").access("#oauth2.hasScope('read') or (!#oauth2.isOAuth() and hasRole('ROLE_USER'))").antMatchers("/photos").access("#oauth2.hasScope('read') or (!#oauth2.isOAuth() and hasRole('ROLE_USER'))")
                .regexMatchers(HttpMethod.DELETE, "/photos/([^/].*?)/,*")
                .access("#oauth2.clientHasRole('ROLE_TRUSTED_CLIENT') and (hasRole('ROLE_USER')) and #oauth2.hasScope('write')")
                .regexMatchers(HttpMethod.POST, "/photos/([^/].*?)/,*")
                .access("#oauth2.clientHasRole('ROLE_TRUSTED_CLIENT') and (hasRole('ROLE_USER')) and #oauth2.hasScope('write')")
                .anyRequest().authenticated();
    }

}
