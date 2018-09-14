package com.revengemission.sso.oauth2.server.service;

import com.revengemission.sso.oauth2.server.domain.JsonObjects;
import com.revengemission.sso.oauth2.server.domain.LoginHistory;

public interface LoginHistoryService extends CommonServiceInterface<LoginHistory> {
    JsonObjects<LoginHistory> listByUsername(String username, int pageNum,
                                             int pageSize,
                                             String sortField,
                                             String sortOrder);
    
    void asyncCreate(LoginHistory loginHistory);

}
