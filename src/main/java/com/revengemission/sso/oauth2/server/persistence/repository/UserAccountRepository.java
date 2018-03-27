package com.revengemission.sso.oauth2.server.persistence.repository;

import com.revengemission.sso.oauth2.server.persistence.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccountEntity, Long> {
    UserAccountEntity findByUsername(String username);
}
