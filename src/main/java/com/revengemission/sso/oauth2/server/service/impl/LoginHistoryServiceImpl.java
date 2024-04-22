package com.revengemission.sso.oauth2.server.service.impl;

import com.revengemission.sso.oauth2.server.domain.AlreadyExistsException;
import com.revengemission.sso.oauth2.server.domain.JsonObjects;
import com.revengemission.sso.oauth2.server.domain.LoginHistory;
import com.revengemission.sso.oauth2.server.mapper.LoginHistoryMapper;
import com.revengemission.sso.oauth2.server.persistence.entity.LoginHistoryEntity;
import com.revengemission.sso.oauth2.server.persistence.repository.LoginHistoryRepository;
import com.revengemission.sso.oauth2.server.service.LoginHistoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginHistoryServiceImpl implements LoginHistoryService {
    @Autowired
    LoginHistoryRepository loginHistoryRepository;

    @Autowired
    LoginHistoryMapper mapper;

    @Override
    public JsonObjects<LoginHistory> listByUsername(String username, int pageNum, int pageSize, String sortField, String sortOrder) {
        JsonObjects<LoginHistory> jsonObjects = new JsonObjects<>();
        Sort sort;
        if (StringUtils.equalsIgnoreCase(sortOrder, "asc")) {
            sort = Sort.by(Sort.Direction.ASC, sortField);
        } else {
            sort = Sort.by(Sort.Direction.DESC, sortField);
        }
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<LoginHistoryEntity> page = loginHistoryRepository.findByUsername(username, pageable);
        if (page.getContent() != null && page.getContent().size() > 0) {
            jsonObjects.setTotal(page.getTotalElements());
            jsonObjects.setPages(page.getTotalPages());
            page.getContent().forEach(u -> jsonObjects.getRows().add(mapper.entityToDto(u)));
        }
        return jsonObjects;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void asyncCreate(LoginHistory loginHistory) throws AlreadyExistsException {
        LoginHistoryEntity entity = mapper.dtoToEntity(loginHistory);
        loginHistoryRepository.save(entity);
    }
}
