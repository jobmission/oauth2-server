package com.revengemission.sso.oauth2.server.config;

import com.revengemission.sso.oauth2.server.domain.GlobalConstant;
import com.revengemission.sso.oauth2.server.domain.VerificationCodeException;
import com.revengemission.sso.oauth2.server.service.CaptchaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.LinkedHashMap;
import java.util.Map;


public class CustomAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private boolean passwordCaptcha;

    private UserDetailsService userService;

    private PasswordEncoder passwordEncoder;

    private CaptchaService captchaService;

    public CustomAuthenticationProvider(UserDetailsService userService, PasswordEncoder passwordEncoder, CaptchaService captchaService, boolean passwordCaptcha) {
        this.passwordCaptcha = passwordCaptcha;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.captchaService = captchaService;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            this.logger.debug("Authentication failed: no credentials provided");
            throw new BadCredentialsException(this.messages
                .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        } else {
            String presentedPassword = authentication.getCredentials().toString();
            if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
                this.logger.debug("Authentication failed: password does not match stored value");
                throw new BadCredentialsException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            }
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
        throws AuthenticationException {

        // 添加额外处理，如验证码等
        Object details = authentication.getDetails();
        if (details instanceof CustomWebAuthenticationDetails) {
            CustomWebAuthenticationDetails customWebAuthenticationDetails = (CustomWebAuthenticationDetails) details;
            String captcha = captchaService.getCaptcha(CachesEnum.GraphCaptchaCache, customWebAuthenticationDetails.getGraphId());
            if (!StringUtils.equalsIgnoreCase(customWebAuthenticationDetails.getInputVerificationCode(), captcha)) {
                throw new VerificationCodeException("验证码错误！");
            }
            captchaService.removeCaptcha(CachesEnum.GraphCaptchaCache, customWebAuthenticationDetails.getGraphId());
        } else if (details instanceof LinkedHashMap<?, ?>) {

            if (passwordCaptcha) {
                @SuppressWarnings("unchecked")
                Map<String, String> map = (Map<String, String>) details;
                if (map.containsKey("grant_type") && StringUtils.equals("password", map.get("grant_type"))) {

                    if (map.containsKey("graphId") && map.containsKey(GlobalConstant.VERIFICATION_CODE)) {
                        String graphId = map.get("graphId");
                        String captcha = captchaService.getCaptcha(CachesEnum.GraphCaptchaCache, graphId);
                        if (!StringUtils.equalsIgnoreCase(map.get(GlobalConstant.VERIFICATION_CODE), captcha)) {
                            throw new VerificationCodeException("验证码错误！");
                        }
                        captchaService.removeCaptcha(CachesEnum.GraphCaptchaCache, graphId);

                    } else {
                        throw new VerificationCodeException("验证码错误！");
                    }
                }
            }

        }

        try {
            UserDetails loadedUser = userService.loadUserByUsername(username);
            if (loadedUser == null) {
                throw new InternalAuthenticationServiceException(
                    "UserDetailsService returned null, which is an interface contract violation");
            }

            return loadedUser;
        } catch (UsernameNotFoundException | InternalAuthenticationServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean supports(Class<?> arg0) {
        return true;
    }

}
