package com.revengemission.sso.oauth2.server.config;

import com.revengemission.sso.oauth2.server.domain.UserInfo;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

public class CustomTokenEnhancer implements TokenEnhancer {

    /**
     * 自定义一些token属性
     *
     * @param accessToken
     * @param authentication
     * @return
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        final Map<String, Object> additionalInformation = new HashMap<>();
        // Important !,client_credentials mode ,no user!
        if (authentication.getUserAuthentication() != null) {
            UserInfo user = (UserInfo) authentication.getUserAuthentication().getPrincipal();// 与登录时候放进去的UserDetail实现类一致
            additionalInformation.put("grantType", authentication.getOAuth2Request().getGrantType());
            additionalInformation.put("accountOpenCode", "" + user.getUserId());
            additionalInformation.put("sub", user.getUsername());
            additionalInformation.put("status", 1);
        }
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
        return accessToken;
    }

}
