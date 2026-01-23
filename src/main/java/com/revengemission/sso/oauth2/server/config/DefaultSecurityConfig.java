package com.revengemission.sso.oauth2.server.config;

import com.revengemission.sso.oauth2.server.domain.RoleEnum;
import com.revengemission.sso.oauth2.server.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * @author Joe Grandja
 * @since 0.1.0
 */
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class DefaultSecurityConfig {

    @Value("${signIn.captcha:false}")
    boolean passwordCaptcha;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    CaptchaService captchaService;

    @Autowired
    CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Autowired
    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Order(2)
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/management/**", "/assets/**", "/favicon.ico", "/signIn", "/signUp", "/security_check", "/404", "/captcha/**", "/.well-known/openid-configuration")
            .authorizeHttpRequests(requestMatcherRegistry ->
                requestMatcherRegistry.requestMatchers("/assets/**", "/favicon.ico", "/signIn", "/signUp", "/security_check", "/404", "/captcha/**", "/.well-known/openid-configuration").permitAll()
                    .requestMatchers("/management/**").hasAnyAuthority(RoleEnum.ROLE_SUPER.name())
                    .requestMatchers("/oauth2/signUp").permitAll()
                    .anyRequest().authenticated())
            .csrf(AbstractHttpConfigurer::disable)
            .logout(logoutCustomizer ->
                logoutCustomizer
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/signIn?out"))
            .formLogin(formLoginCustomizer ->
                formLoginCustomizer
                    .failureHandler(customAuthenticationFailureHandler)
                    .successHandler(customAuthenticationSuccessHandler)
                    .loginPage("/signIn")
                    .loginProcessingUrl("/security_check"))
            .oauth2Login(oauth2Login ->
                oauth2Login
                    .loginPage("/signIn")
                    .successHandler(authenticationSuccessHandler())
            )
            .exceptionHandling(exceptionHandlingCustomizer -> exceptionHandlingCustomizer.accessDeniedHandler(customAccessDeniedHandler));

        return http.build();
    }

    private AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new FederatedIdentityAuthenticationSuccessHandler();
    }

    @Bean
    SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

}
