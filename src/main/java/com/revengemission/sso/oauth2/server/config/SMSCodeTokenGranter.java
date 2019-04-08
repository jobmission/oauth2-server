package com.revengemission.sso.oauth2.server.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

public class SMSCodeTokenGranter extends AbstractTokenGranter {

	private static final String GRANT_TYPE = "sms_code";

	UserDetailsService userDetailsService;

	public SMSCodeTokenGranter(UserDetailsService userDetailsService,
			AuthorizationServerTokenServices authorizationServerTokenServices,
			ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
		super(authorizationServerTokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

		Map<String, String> parameters = new LinkedHashMap<String, String>(tokenRequest.getRequestParameters());
		String userMobileNo = parameters.get("username"); // 客户端提交的用户名
		String smscode = parameters.get("smscode"); // 客户端提交的验证码

		// 从库里查用户
		UserDetails user = userDetailsService.loadUserByUsername(userMobileNo);
		if (user == null) {
			throw new InvalidGrantException("用户不存在");
		}

		// 验证用户状态(是否警用等),代码略
		// 验证验证码
		String smsCodeCached = "abcd";
		if (StringUtils.isBlank(smsCodeCached)) {
			throw new InvalidGrantException("用户没有发送验证码");
		}
		if (!smscode.equals(smsCodeCached)) {
			throw new InvalidGrantException("验证码不正确");
		} else {
			// 验证通过后从缓存中移除验证码,代码略
		}

		Authentication userAuth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		// 关于user.getAuthorities(): 我们的自定义用户实体是实现了
		// org.springframework.security.core.userdetails.UserDetails 接口的, 所以有
		// user.getAuthorities()
		// 当然该参数传null也行
		((AbstractAuthenticationToken) userAuth).setDetails(parameters);

		OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
		return new OAuth2Authentication(storedOAuth2Request, userAuth);
	}

}
