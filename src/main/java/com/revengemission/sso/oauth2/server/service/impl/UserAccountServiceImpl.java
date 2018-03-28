package com.revengemission.sso.oauth2.server.service.impl;

import com.revengemission.sso.oauth2.server.domain.EntityNotFoundException;
import com.revengemission.sso.oauth2.server.domain.JsonObjects;
import com.revengemission.sso.oauth2.server.domain.UserAccount;
import com.revengemission.sso.oauth2.server.persistence.entity.UserAccountEntity;
import com.revengemission.sso.oauth2.server.persistence.repository.UserAccountRepository;
import com.revengemission.sso.oauth2.server.service.UserAccountService;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    @Autowired
    UserAccountRepository userAccountRepository;

    @Autowired
    Mapper dozerMapper;

    @Override
    public JsonObjects<UserAccount> listByRole(String role, int pageNum, int pageSize, String sortField, String sortOrder) {
        JsonObjects<UserAccount> jsonObjects = new JsonObjects<>();
        Sort sort = null;
        if (StringUtils.equalsIgnoreCase(sortOrder, "asc")) {
            sort = new Sort(Sort.Direction.ASC, sortField);
        } else {
            sort = new Sort(Sort.Direction.DESC, sortField);
        }
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<UserAccountEntity> page = userAccountRepository.findByRole(role, pageable);
        if (page.getContent() != null && page.getContent().size() > 0) {
            jsonObjects.setCurrentPage(pageNum);
            jsonObjects.setTotalPage(page.getTotalPages());
            page.getContent().forEach(u -> {
                jsonObjects.getObjectElements().add(dozerMapper.map(u, UserAccount.class));
            });
        }
        return jsonObjects;

    }

    @Override
    public UserAccount create(UserAccount userAccount) {
        UserAccountEntity userAccountEntity = dozerMapper.map(userAccount, UserAccountEntity.class);
        userAccountRepository.save(userAccountEntity);
        return dozerMapper.map(userAccountEntity, UserAccount.class);
    }

    @Override
    public UserAccount updateById(UserAccount userAccount) throws EntityNotFoundException {
        Optional<UserAccountEntity> entityOptional = userAccountRepository.findById(Long.parseLong(userAccount.getId()));
        entityOptional.orElseThrow(EntityNotFoundException::new);
        entityOptional.ifPresent(e -> {
            UserAccountEntity userAccountEntity = entityOptional.get();
            if (StringUtils.isNotEmpty(userAccount.getPassword())) {
                userAccountEntity.setPassword(userAccount.getPassword());
            }
            userAccountEntity.setNickName(userAccount.getNickName());
            userAccountEntity.setBirthday(userAccount.getBirthday());
            userAccountEntity.setMobile(userAccount.getMobile());
            userAccountEntity.setProvince(userAccount.getProvince());
            userAccountEntity.setCity(userAccount.getCity());
            userAccountEntity.setAddress(userAccount.getAddress());
            userAccountEntity.setAvatarUrl(userAccount.getAvatarUrl());
            userAccountEntity.setEmail(userAccount.getEmail());

            userAccountRepository.save(userAccountEntity);
        });
        return userAccount;
    }

    @Override
    public UserAccount findByUsername(String username) throws EntityNotFoundException {
        UserAccountEntity userAccountEntity = userAccountRepository.findByUsername(username);
        if (userAccountEntity != null) {
            return dozerMapper.map(userAccountEntity, UserAccount.class);
        } else {
            throw new EntityNotFoundException(username + " not found!");
        }
    }
}
