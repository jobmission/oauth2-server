package com.revengemission.sso.oauth2.server.service;

import com.revengemission.sso.oauth2.server.domain.EntityNotFoundException;
import com.revengemission.sso.oauth2.server.domain.NotImplementException;
import com.revengemission.sso.oauth2.server.domain.OauthClient;

public interface OauthClientService extends CommonServiceInterface<OauthClient> {
    default OauthClient findByClientId(String clientId) throws EntityNotFoundException {
        throw new NotImplementException();
    }
}
