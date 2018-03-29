package com.revengemission.sso.oauth2.server.service.impl;

import com.revengemission.sso.oauth2.server.domain.AlreadyExistsException;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    @Autowired
    UserAccountRepository userAccountRepository;

    @Autowired
    Mapper dozerMapper;

    @Override
    public JsonObjects<UserAccount> listByRole(String role, String username, int pageNum, int pageSize, String sortField, String sortOrder) {
        JsonObjects<UserAccount> jsonObjects = new JsonObjects<>();
        Sort sort = null;
        if (StringUtils.equalsIgnoreCase(sortOrder, "asc")) {
            sort = new Sort(Sort.Direction.ASC, sortField);
        } else {
            sort = new Sort(Sort.Direction.DESC, sortField);
        }
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<UserAccountEntity> page = userAccountRepository.findByRoleAndUsernameLike(role, username + "%", pageable);
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
    @Transactional
    public UserAccount create(UserAccount userAccount) throws AlreadyExistsException {
        UserAccountEntity exist = userAccountRepository.findByUsername(userAccount.getUsername());
        if (exist != null) {
            throw new AlreadyExistsException(userAccount.getUsername() + " already exists!");
        }
        UserAccountEntity userAccountEntity = dozerMapper.map(userAccount, UserAccountEntity.class);
        userAccountRepository.save(userAccountEntity);
        return dozerMapper.map(userAccountEntity, UserAccount.class);
    }

    @Override
    public UserAccount retrieveById(long id) throws EntityNotFoundException {
        Optional<UserAccountEntity> entityOptional = userAccountRepository.findById(id);
        entityOptional.orElseThrow(EntityNotFoundException::new);
        return dozerMapper.map(entityOptional.get(), UserAccount.class);
    }

    @Override
    public UserAccount updateById(UserAccount userAccount) throws EntityNotFoundException {
        Optional<UserAccountEntity> entityOptional = userAccountRepository.findById(Long.parseLong(userAccount.getId()));
        entityOptional.orElseThrow(EntityNotFoundException::new);
        entityOptional.ifPresent(e -> {
            if (StringUtils.isNotEmpty(userAccount.getPassword())) {
                e.setPassword(userAccount.getPassword());
            }
            e.setNickName(userAccount.getNickName());
            e.setBirthday(userAccount.getBirthday());
            e.setMobile(userAccount.getMobile());
            e.setProvince(userAccount.getProvince());
            e.setCity(userAccount.getCity());
            e.setAddress(userAccount.getAddress());
            e.setAvatarUrl(userAccount.getAvatarUrl());
            e.setEmail(userAccount.getEmail());

            userAccountRepository.save(e);
        });
        return userAccount;
    }

    @Override
    public void updateRecordStatus(long id, int recordStatus) {
        Optional<UserAccountEntity> entityOptional = userAccountRepository.findById(id);
        entityOptional.orElseThrow(EntityNotFoundException::new);
        entityOptional.ifPresent(e -> {
            e.setRecordStatus(recordStatus);
            userAccountRepository.save(e);
        });
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
