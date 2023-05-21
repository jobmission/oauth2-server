package com.revengemission.sso.oauth2.server.config;

import com.revengemission.sso.oauth2.server.persistence.entity.OauthClientEntity;
import com.revengemission.sso.oauth2.server.persistence.repository.OauthClientRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

public class JdbcClientRegistrationRepository implements ClientRegistrationRepository {

    OauthClientRepository oauthClientRepository;

    public JdbcClientRegistrationRepository(OauthClientRepository oauthClientRepository) {
        this.oauthClientRepository = oauthClientRepository;
    }

    ClientRegistration convertOauthClientEntityToClientRegistration(OauthClientEntity oauthClientEntity) {
        ClientRegistration clientRegistration = ClientRegistration.
            withRegistrationId(oauthClientEntity.getId() + "")
            .clientId(oauthClientEntity.getClientId())
            .clientSecret(oauthClientEntity.getClientSecret())
            .clientName(oauthClientEntity.getApplicationName())
            .build();
        return clientRegistration;
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {

        OauthClientEntity oauthClientEntity = oauthClientRepository.findByClientId(registrationId);
        if (oauthClientEntity != null) {
            ClientRegistration clientRegistration = convertOauthClientEntityToClientRegistration(oauthClientEntity);
            return clientRegistration;
        } else {
            return null;
        }
    }
}
