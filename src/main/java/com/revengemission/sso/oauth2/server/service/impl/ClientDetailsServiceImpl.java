package com.revengemission.sso.oauth2.server.service.impl;

import com.revengemission.sso.oauth2.server.domain.AlreadyExpiredException;
import com.revengemission.sso.oauth2.server.domain.InvalidClientException;
import com.revengemission.sso.oauth2.server.persistence.entity.OauthClientEntity;
import com.revengemission.sso.oauth2.server.persistence.repository.OauthClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ClientDetailsServiceImpl implements ClientDetailsService {

    @Autowired
    OauthClientRepository oauthClientRepository;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {

        OauthClientEntity oauthClientEntity = oauthClientRepository.findByClientId(clientId);
        if (oauthClientEntity != null) {
            if (oauthClientEntity.getRecordStatus() < 0) {
                throw new InvalidClientException(String.format("clientId %s is disabled!", clientId));
            }
            if (oauthClientEntity.getExpirationDate() != null && oauthClientEntity.getExpirationDate().compareTo(new Date()) < 0) {
                throw new AlreadyExpiredException(String.format("clientId %s already expired!", clientId));
            }
            BaseClientDetails baseClientDetails = new BaseClientDetails();
            baseClientDetails.setClientId(oauthClientEntity.getClientId());
            if (!StringUtils.isEmpty(oauthClientEntity.getResourceIds())) {
                baseClientDetails.setResourceIds(StringUtils.commaDelimitedListToSet(oauthClientEntity.getResourceIds()));
            }
            baseClientDetails.setClientSecret(oauthClientEntity.getClientSecret());
            if (!StringUtils.isEmpty(oauthClientEntity.getScope())) {
                baseClientDetails.setScope(StringUtils.commaDelimitedListToSet(oauthClientEntity.getScope()));
            }
            if (!StringUtils.isEmpty(oauthClientEntity.getAuthorizedGrantTypes())) {
                baseClientDetails.setAuthorizedGrantTypes(StringUtils.commaDelimitedListToSet(oauthClientEntity.getAuthorizedGrantTypes()));
            } else {
                baseClientDetails.setAuthorizedGrantTypes(StringUtils.commaDelimitedListToSet("authorization_code"));
            }
            if (!StringUtils.isEmpty(oauthClientEntity.getWebServerRedirectUri())) {
                baseClientDetails.setRegisteredRedirectUri(StringUtils.commaDelimitedListToSet(oauthClientEntity.getWebServerRedirectUri()));
            }
            if (!StringUtils.isEmpty(oauthClientEntity.getAuthorities())) {
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                StringUtils.commaDelimitedListToSet(oauthClientEntity.getAuthorities()).forEach(s -> {
                    authorities.add(new SimpleGrantedAuthority(s));
                });
                baseClientDetails.setAuthorities(authorities);
            }
            if (oauthClientEntity.getAccessTokenValidity() != null && oauthClientEntity.getAccessTokenValidity() > 0) {
                baseClientDetails.setAccessTokenValiditySeconds(oauthClientEntity.getAccessTokenValidity());
            }
            if (oauthClientEntity.getRefreshTokenValidity() != null && oauthClientEntity.getRefreshTokenValidity() > 0) {
                baseClientDetails.setRefreshTokenValiditySeconds(oauthClientEntity.getRefreshTokenValidity());
            }
            //baseClientDetails.setAdditionalInformation(oauthClientEntity.getAdditionalInformation());
            if (!StringUtils.isEmpty(oauthClientEntity.getAutoApprove())) {
                baseClientDetails.setAutoApproveScopes(StringUtils.commaDelimitedListToSet(oauthClientEntity.getAutoApprove()));
            }
            return baseClientDetails;
        } else {
            throw new NoSuchClientException("No client with requested id: " + clientId);
        }
    }
}
