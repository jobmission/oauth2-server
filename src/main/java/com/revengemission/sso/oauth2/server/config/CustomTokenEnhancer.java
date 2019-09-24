package com.revengemission.sso.oauth2.server.config;

import com.revengemission.sso.oauth2.server.domain.UserInfo;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

public class CustomTokenEnhancer implements TokenEnhancer {

    private String issuerUri;
    private JsonParser objectMapper = JsonParserFactory.create();

    public CustomTokenEnhancer(String issuerUri) {
        this.issuerUri = issuerUri;
    }

    /**
     * 自定义一些token属性
     *
     * @param accessToken    accessToken
     * @param authentication authentication
     * @return OAuth2AccessToken
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        final Map<String, Object> additionalInformation = new HashMap<>(16);
        if (authentication.getOAuth2Request().isRefresh()) {
            Map<String, Object> map = decode(accessToken.getRefreshToken().getValue());
            if (map.containsKey("accountOpenCode")) {
                additionalInformation.put("accountOpenCode", map.get("accountOpenCode"));
            }
            if (map.containsKey("grantType")) {
                additionalInformation.put("grantType", map.get("grantType"));
            }
            if (map.containsKey("sub")) {
                additionalInformation.put("sub", map.get("sub"));
            } else {
                additionalInformation.put("sub", authentication.getUserAuthentication().getName());
            }

            additionalInformation.put("iss", this.issuerUri);
        } else {
            // Important !,client_credentials mode ,no user!
            if (authentication.getUserAuthentication() != null) {
                // 与登录时候放进去的UserDetail实现类一致
                UserInfo user = (UserInfo) authentication.getUserAuthentication().getPrincipal();
                additionalInformation.put("grantType", authentication.getOAuth2Request().getGrantType());
                additionalInformation.put("accountOpenCode", user.getAccountOpenCode());
                additionalInformation.put("sub", user.getUsername());
            }
            additionalInformation.put("iss", this.issuerUri);
        }

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
        return accessToken;
    }

    protected Map<String, Object> decode(String token) {
        try {
            Jwt jwt = JwtHelper.decode(token);
            String claimsStr = jwt.getClaims();
            Map<String, Object> claims = objectMapper.parseMap(claimsStr);
            return claims;
        } catch (Exception e) {
            throw new InvalidTokenException("Cannot convert access token to JSON", e);
        }
    }
}
