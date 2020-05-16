package com.revengemission.sso.oauth2.server.token;

import com.revengemission.sso.oauth2.server.domain.OauthClient;

import java.util.Map;

public interface TokenGranter {

    Map<String, Object> grant(OauthClient client, String grantType, Map<String, String> parameters);

    default void validateGrantType(OauthClient client, String grantType) {

    }
}
