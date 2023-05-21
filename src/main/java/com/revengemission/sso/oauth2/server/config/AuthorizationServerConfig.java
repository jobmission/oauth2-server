package com.revengemission.sso.oauth2.server.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.revengemission.sso.oauth2.server.jose.Jwks;
import com.revengemission.sso.oauth2.server.persistence.repository.OauthClientRepository;
import com.revengemission.sso.oauth2.server.service.impl.RegisteredClientRepositoryImpl;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

/**
 * @author Joe Grandja
 * @since 0.0.1
 */
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {

    private static final String CUSTOM_CONSENT_PAGE_URI = "/oauth2/consent";

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      RegisteredClientRepository registeredClientRepository,
                                                                      AuthorizationServerSettings authorizationServerSettings) throws Exception {

        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        /**
         * Customize the User Info response
         * https://docs.spring.io/spring-authorization-server/docs/current/reference/html/guides/how-to-userinfo.html#customize-user-info
         */
//        Function<OidcUserInfoAuthenticationContext, OidcUserInfo> userInfoMapper = (context) -> {
//            OidcUserInfoAuthenticationToken authentication = context.getAuthentication();
//            JwtAuthenticationToken principal = (JwtAuthenticationToken) authentication.getPrincipal();
//
//            return new OidcUserInfo(principal.getToken().getClaims());
//        };
//
//        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
//        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();
//
//        authorizationServerConfigurer
//            .oidc((oidc) -> oidc
//                .userInfoEndpoint((userInfo) -> userInfo
//                    .userInfoMapper(userInfoMapper)
//                )
//            );
        DeviceClientAuthenticationConverter deviceClientAuthenticationConverter =
            new DeviceClientAuthenticationConverter(
                authorizationServerSettings.getDeviceAuthorizationEndpoint());
        DeviceClientAuthenticationProvider deviceClientAuthenticationProvider =
            new DeviceClientAuthenticationProvider(registeredClientRepository);


        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
            .clientAuthentication(clientAuthentication ->
                clientAuthentication
                    .authenticationConverter(deviceClientAuthenticationConverter)
                    .authenticationProvider(deviceClientAuthenticationProvider)
            )
            .authorizationEndpoint(authorizationEndpoint ->
                authorizationEndpoint.consentPage(CUSTOM_CONSENT_PAGE_URI))
            .oidc(Customizer.withDefaults());

        http
            .exceptionHandling((exceptions) -> exceptions
                .defaultAuthenticationEntryPointFor(
                    new LoginUrlAuthenticationEntryPoint("/signIn"),
                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                )
            )
            .oauth2ResourceServer(oauth2ResourceServer ->
                oauth2ResourceServer.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(OauthClientRepository oauthClientRepository, CacheManager cacheManager) {
        RegisteredClientRepositoryImpl registeredClientRepository = new RegisteredClientRepositoryImpl(oauthClientRepository, cacheManager);
        return registeredClientRepository;
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(OauthClientRepository oauthClientRepository) {
        return new JdbcClientRegistrationRepository(oauthClientRepository);
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = Jwks.generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

}
