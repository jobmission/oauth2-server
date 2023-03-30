//package com.revengemission.sso.oauth2.server.config;
//
//import com.revengemission.sso.oauth2.server.domain.RoleEnum;
//import com.revengemission.sso.oauth2.server.service.CaptchaService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationDetailsSource;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.authentication.WebAuthenticationDetails;
//
//import javax.servlet.http.HttpServletRequest;
//
//@Configuration
//public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
//
//    @Value("${signIn.captcha:false}")
//    private boolean passwordCaptcha;
//
//    @Autowired
//    CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
//
//    @Autowired
//    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
//
//    @Autowired
//    CustomAccessDeniedHandler customAccessDeniedHandler;
//
//    @Autowired
//    AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource;
//
//    @Autowired
//    CaptchaService captchaService;
//
//    @Autowired
//    UserDetailsService userService;
//
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    AuthenticationProvider authenticationProvider() {
//        return new CustomAuthenticationProvider(userService, passwordEncoder(), captchaService, passwordCaptcha);
//    }
//
//    @Override
//    public void configure(WebSecurity web) {
//        web.ignoring().antMatchers("/assets/**");
//    }
//
//    @Override
//    @Bean
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
/////        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
//        http
//            .authorizeRequests()
//            .mvcMatchers("/favicon.ico", "/signIn", "/signUp", "/security_check", "/404", "/captcha/**", "/user/me", "/oauth/check_token", "/oauth/token", "/.well-known/**").permitAll()
//            .mvcMatchers("/oauth/signUp").permitAll()
//            .mvcMatchers("/management/**").hasAnyAuthority(RoleEnum.ROLE_SUPER.name())
//            .anyRequest().authenticated()
//            .and()
//            .csrf().disable()
//            .logout()
//            .logoutUrl("/logout")
//            .logoutSuccessUrl("/signIn?out")
//            .and()
//            .formLogin()
//            .authenticationDetailsSource(authenticationDetailsSource)
//            .failureHandler(customAuthenticationFailureHandler)
//            .successHandler(customAuthenticationSuccessHandler)
//            .loginPage("/signIn").loginProcessingUrl("/security_check").permitAll();
//
//        http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler);
//    }
//}
