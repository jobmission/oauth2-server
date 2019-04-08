package com.revengemission.sso.oauth2.server.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.revengemission.sso.oauth2.server.domain.UserInfo;

public class CustomTokenEnhancer implements TokenEnhancer{
	
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		/** 自定义一些token属性 ***/
		final Map<String, Object> additionalInformation = new HashMap<>();
		// Important !,client_credentials mode ,no user!
		if (authentication.getUserAuthentication() != null) {
			UserInfo user = (UserInfo) authentication.getUserAuthentication().getPrincipal();// 与登录时候放进去的UserDetail实现类一致
			additionalInformation.put("userId", user.getUserId());
		}
		 ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
		 return accessToken;
	}
	
}
