package com.revengemission.sso.oauth2.server.service.impl;

import com.github.dozermapper.core.Mapper;
import com.revengemission.sso.oauth2.server.domain.NotImplementException;
import com.revengemission.sso.oauth2.server.domain.Role;
import com.revengemission.sso.oauth2.server.persistence.entity.RoleEntity;
import com.revengemission.sso.oauth2.server.persistence.repository.RoleRepository;
import com.revengemission.sso.oauth2.server.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    Mapper dozerMapper;

    @Override
    public Role findByRoleName(String roleName) throws NotImplementException {
        RoleEntity roleEntity = roleRepository.findByRoleName(roleName);
        if (roleEntity != null) {
            return dozerMapper.map(roleEntity, Role.class);
        } else {
            return null;
        }
    }

}
