package com.revengemission.sso.oauth2.server.service.impl;

import com.revengemission.sso.oauth2.server.config.CachesEnum;
import com.revengemission.sso.oauth2.server.persistence.entity.OauthClientEntity;
import com.revengemission.sso.oauth2.server.persistence.repository.OauthClientRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

public class RegisteredClientRepositoryImpl implements RegisteredClientRepository {

    OauthClientRepository oauthClientRepository;

    CacheManager cacheManager;

    public RegisteredClientRepositoryImpl(OauthClientRepository oauthClientRepository, CacheManager cacheManager) {
        this.oauthClientRepository = oauthClientRepository;
        this.cacheManager = cacheManager;
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        System.out.println("RegisteredClientRepository.save() called");
    }

    @Override
    public RegisteredClient findById(String id) {

        OauthClientEntity oauthClientEntity = oauthClientRepository.findById(Long.parseLong(id)).orElse(null);
        if (oauthClientEntity != null) {
            RegisteredClient registeredClient = convertOauthClientEntityToRegisteredClient(oauthClientEntity);
            return registeredClient;
        } else {
            return null;
        }
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        Cache.ValueWrapper valueWrapper = cacheManager.getCache(CachesEnum.Oauth2ClientCache.name()).get(clientId);

        if (valueWrapper != null) {
            return (RegisteredClient) valueWrapper.get();
        }

        OauthClientEntity oauthClientEntity = oauthClientRepository.findByClientId(clientId);
        if (oauthClientEntity != null) {
            RegisteredClient registeredClient = convertOauthClientEntityToRegisteredClient(oauthClientEntity);
            cacheManager.getCache(CachesEnum.Oauth2ClientCache.name()).put(clientId, registeredClient);
            return registeredClient;
        } else {
            return null;
        }
    }

    RegisteredClient convertOauthClientEntityToRegisteredClient(OauthClientEntity oauthClientEntity) {
        TokenSettings tokenSettings = TokenSettings.builder()
            .accessTokenTimeToLive(Duration.ofSeconds(oauthClientEntity.getAccessTokenValidity()))
            .refreshTokenTimeToLive(Duration.ofSeconds(oauthClientEntity.getRefreshTokenValidity()))
            .reuseRefreshTokens(true)
            .authorizationCodeTimeToLive(Duration.ofMinutes(5))
            .build();

        RegisteredClient registeredClient = RegisteredClient
            .withId(String.valueOf(oauthClientEntity.getId()))
            .clientId(oauthClientEntity.getClientId())
            .clientSecret(oauthClientEntity.getClientSecret())
            .clientName(oauthClientEntity.getApplicationName())
            .redirectUris(set -> set.addAll(StringUtils.commaDelimitedListToSet(oauthClientEntity.getResourceIds())))
            .authorizationGrantTypes(set -> set.addAll(StringUtils.commaDelimitedListToSet(oauthClientEntity.getAuthorizedGrantTypes()).stream().map(s -> new AuthorizationGrantType(s)).collect(Collectors.toList())))
            .clientAuthenticationMethods(set -> set.addAll(Arrays.asList(ClientAuthenticationMethod.CLIENT_SECRET_BASIC, ClientAuthenticationMethod.CLIENT_SECRET_POST)))
            .scopes(set -> set.addAll(StringUtils.commaDelimitedListToSet(oauthClientEntity.getScope())))
            .redirectUris(set -> set.addAll(StringUtils.commaDelimitedListToSet(oauthClientEntity.getWebServerRedirectUri())))
            .tokenSettings(tokenSettings)
            .build();
        return registeredClient;
    }

}
