package com.revengemission.sso.oauth2.server.persistence.repository;

import com.revengemission.sso.oauth2.server.persistence.entity.OauthClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthClientRepository extends JpaRepository<OauthClientEntity, Long> {
    OauthClientEntity findByClientId(String clientId);
}
