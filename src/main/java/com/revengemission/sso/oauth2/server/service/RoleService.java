package com.revengemission.sso.oauth2.server.service;

import com.revengemission.sso.oauth2.server.domain.NotImplementException;
import com.revengemission.sso.oauth2.server.domain.Role;

public interface RoleService extends CommonServiceInterface<Role> {
    default Role findByRoleName(String roleName) throws NotImplementException {
        throw new NotImplementException();
    }
}
