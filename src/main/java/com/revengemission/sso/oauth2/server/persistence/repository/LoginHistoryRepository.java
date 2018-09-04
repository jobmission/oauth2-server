package com.revengemission.sso.oauth2.server.persistence.repository;

import com.revengemission.sso.oauth2.server.persistence.entity.LoginHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginHistoryRepository extends JpaRepository<LoginHistoryEntity, Long> {
    Page<LoginHistoryEntity> findByUsername(String username, Pageable page);
}
