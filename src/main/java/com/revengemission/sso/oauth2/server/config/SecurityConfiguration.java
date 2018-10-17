package com.revengemission.sso.oauth2.server.config;

import com.revengemission.sso.oauth2.server.domain.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


    /*@Autowired
    UserDetailsService userDetailsService;*/

    @Autowired
    CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Autowired
    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource;

    @Autowired
    AuthenticationProvider customAuthenticationProvider;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

/*    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("zhangsan").password("password").roles("USER").and()
//                .withUser("lisi").password("password").roles("USER");

        //auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(customAuthenticationProvider);//重点
    }*/


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/assets/**");
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
        http
                .authorizeRequests()
                .antMatchers("/signIn", "/signUp", "/security_check", "/404", "/captcha").permitAll()
                .antMatchers("/oauth/signUp").permitAll()
                .antMatchers("/management/**").hasAnyAuthority(RoleEnum.ROLE_ADMIN.name(), RoleEnum.ROLE_SUPER.name())
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/signIn?out")
                .and()
                .formLogin()
                .authenticationDetailsSource(authenticationDetailsSource) //重点
                .failureHandler(customAuthenticationFailureHandler)
                .successHandler(customAuthenticationSuccessHandler)
                .loginPage("/signIn").loginProcessingUrl("/security_check").permitAll();

        http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler);
    }
}
