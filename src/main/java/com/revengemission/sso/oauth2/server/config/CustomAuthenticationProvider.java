package com.revengemission.sso.oauth2.server.config;

import com.revengemission.sso.oauth2.server.domain.UserAccount;
import com.revengemission.sso.oauth2.server.domain.VerificationCodeException;
import com.revengemission.sso.oauth2.server.service.UserAccountService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Created by SMT-24 on 2017/12/8.
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    UserAccountService userService;

    /**
     * 自定义验证方式
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        CustomWebAuthenticationDetails customWebAuthenticationDetails = (CustomWebAuthenticationDetails) authentication.getDetails();
        if (!StringUtils.equalsIgnoreCase(customWebAuthenticationDetails.getInputVerificationCode(), customWebAuthenticationDetails.getSessionVerificationCode())) {
            throw new VerificationCodeException("验证码错误！");
        }

        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        UserAccount userAccount = userService.findByUsername(username);
        if (userAccount == null) {
            throw new BadCredentialsException("Username not found.");
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        //加密过程在这里体现
        if (!bCryptPasswordEncoder.matches(password, userAccount.getPassword())) {
            throw new BadCredentialsException("Wrong password.");
        }
        Collection<? extends GrantedAuthority> authorities = getAuthorities(userAccount);
        //Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        return new UsernamePasswordAuthenticationToken(userAccount.getUsername(), password, authorities);
    }

    @Override
    public boolean supports(Class<?> arg0) {
        return true;
    }

    public Collection<? extends GrantedAuthority> getAuthorities(UserAccount user) {
        return AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRole());
    }
}

