package com.revengemission.sso.oauth2.server.service;

import com.revengemission.sso.oauth2.server.domain.EntityNotFoundException;
import com.revengemission.sso.oauth2.server.domain.JsonObjects;
import com.revengemission.sso.oauth2.server.domain.UserAccount;

public interface UserAccountService extends CommonServiceInterface<UserAccount> {
    JsonObjects<UserAccount> listByRole(String role, String username, int pageNum,
                                        int pageSize,
                                        String sortField,
                                        String sortOrder);

    UserAccount findByUsername(String username) throws EntityNotFoundException;

    void loginSuccess(String username) throws EntityNotFoundException;

    void loginFailure(String username);
}
