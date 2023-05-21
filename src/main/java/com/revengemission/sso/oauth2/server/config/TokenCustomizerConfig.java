package com.revengemission.sso.oauth2.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.Collection;
import java.util.stream.Collectors;

@Configuration
public class TokenCustomizerConfig {

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(UserDetailsService userDetailsService) {
        return (context) -> {
            UserDetails userDetails = userDetailsService.loadUserByUsername(
                context.getPrincipal().getName());
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            context.getClaims().claims(claims ->
                claims.put("authorities", authorities.stream().map(authority -> authority.getAuthority()).collect(Collectors.toList())));
        };
    }

}


