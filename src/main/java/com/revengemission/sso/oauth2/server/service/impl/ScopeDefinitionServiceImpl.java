package com.revengemission.sso.oauth2.server.service.impl;

import com.revengemission.sso.oauth2.server.domain.NotImplementException;
import com.revengemission.sso.oauth2.server.domain.ScopeDefinition;
import com.revengemission.sso.oauth2.server.mapper.ScopeDefinitionMapper;
import com.revengemission.sso.oauth2.server.persistence.entity.ScopeDefinitionEntity;
import com.revengemission.sso.oauth2.server.persistence.repository.ScopeDefinitionRepository;
import com.revengemission.sso.oauth2.server.service.ScopeDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScopeDefinitionServiceImpl implements ScopeDefinitionService {

    @Autowired
    ScopeDefinitionRepository scopeDefinitionRepository;

    @Autowired
    ScopeDefinitionMapper mapper;

    @Override
    public ScopeDefinition findByScope(String scope) throws NotImplementException {
        ScopeDefinitionEntity scopeDefinitionEntity = scopeDefinitionRepository.findByScope(scope);
        if (scopeDefinitionEntity != null) {
            return mapper.entityToDto(scopeDefinitionEntity);
        } else {
            return null;
        }
    }

}
