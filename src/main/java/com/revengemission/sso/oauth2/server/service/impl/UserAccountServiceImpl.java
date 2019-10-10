package com.revengemission.sso.oauth2.server.service.impl;

import com.github.dozermapper.core.Mapper;
import com.revengemission.sso.oauth2.server.domain.AlreadyExistsException;
import com.revengemission.sso.oauth2.server.domain.EntityNotFoundException;
import com.revengemission.sso.oauth2.server.domain.JsonObjects;
import com.revengemission.sso.oauth2.server.domain.UserAccount;
import com.revengemission.sso.oauth2.server.persistence.entity.RoleEntity;
import com.revengemission.sso.oauth2.server.persistence.entity.UserAccountEntity;
import com.revengemission.sso.oauth2.server.persistence.repository.RoleRepository;
import com.revengemission.sso.oauth2.server.persistence.repository.UserAccountRepository;
import com.revengemission.sso.oauth2.server.service.UserAccountService;
import com.revengemission.sso.oauth2.server.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    @Autowired
    UserAccountRepository userAccountRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    Mapper dozerMapper;

    @Value("${signin.failure.max:5}")
    private int failureMax;

    @Override
    public JsonObjects<UserAccount> listByUsername(String username, int pageNum, int pageSize, String sortField, String sortOrder) {
        JsonObjects<UserAccount> jsonObjects = new JsonObjects<>();
        Sort sort;
        if (StringUtils.equalsIgnoreCase(sortOrder, "asc")) {
            sort = Sort.by(Sort.Direction.ASC, sortField);
        } else {
            sort = Sort.by(Sort.Direction.DESC, sortField);
        }
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<UserAccountEntity> page;
        if (StringUtils.isBlank(username)) {
            page = userAccountRepository.findAll(pageable);
        } else {
            page = userAccountRepository.findByUsernameLike(username + "%", pageable);
        }
        if (page.getContent() != null && page.getContent().size() > 0) {
            jsonObjects.setRecordsTotal(page.getTotalElements());
            jsonObjects.setRecordsFiltered(page.getTotalElements());
            page.getContent().forEach(u -> jsonObjects.getData().add(dozerMapper.map(u, UserAccount.class)));
        }
        return jsonObjects;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAccount create(UserAccount userAccount) throws AlreadyExistsException {
        UserAccountEntity exist = userAccountRepository.findByUsername(userAccount.getUsername());
        if (exist != null) {
            throw new AlreadyExistsException(userAccount.getUsername() + " already exists!");
        }
        UserAccountEntity userAccountEntity = dozerMapper.map(userAccount, UserAccountEntity.class);
        userAccountEntity.getRoles().clear();
        if (userAccount.getRoles() != null && userAccount.getRoles().size() > 0) {
            userAccount.getRoles().forEach(e -> {
                RoleEntity roleEntity = roleRepository.findByRoleName(e.getRoleName());
                if (roleEntity != null) {
                    userAccountEntity.getRoles().add(roleEntity);
                }
            });
        }
        userAccountRepository.save(userAccountEntity);
        return dozerMapper.map(userAccountEntity, UserAccount.class);
    }

    @Override
    public UserAccount retrieveById(long id) throws EntityNotFoundException {
        Optional<UserAccountEntity> entityOptional = userAccountRepository.findById(id);
        return dozerMapper.map(entityOptional.orElseThrow(EntityNotFoundException::new), UserAccount.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAccount updateById(UserAccount userAccount) throws EntityNotFoundException {
        Optional<UserAccountEntity> entityOptional = userAccountRepository.findById(Long.parseLong(userAccount.getId()));
        UserAccountEntity e = entityOptional.orElseThrow(EntityNotFoundException::new);
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
        return dozerMapper.map(e, UserAccount.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRecordStatus(long id, int recordStatus) {
        Optional<UserAccountEntity> entityOptional = userAccountRepository.findById(id);
        UserAccountEntity e = entityOptional.orElseThrow(EntityNotFoundException::new);
        e.setRecordStatus(recordStatus);
        userAccountRepository.save(e);
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

    @Override
    public boolean existsByUsername(String username) {
        return userAccountRepository.existsByUsername(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void loginSuccess(String username) throws EntityNotFoundException {
        UserAccountEntity userAccountEntity = userAccountRepository.findByUsername(username);
        if (userAccountEntity != null) {
            userAccountEntity.setFailureCount(0);
            userAccountEntity.setFailureTime(null);
            userAccountRepository.save(userAccountEntity);
        } else {
            throw new EntityNotFoundException(username + " not found!");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void loginFailure(String username) {
        UserAccountEntity userAccountEntity = userAccountRepository.findByUsername(username);
        if (userAccountEntity != null) {
            if (userAccountEntity.getFailureTime() == null) {
                userAccountEntity.setFailureCount(1);
            } else {
                if (DateUtil.beforeToday(userAccountEntity.getFailureTime())) {
                    userAccountEntity.setFailureCount(0);
                } else {
                    userAccountEntity.setFailureCount(userAccountEntity.getFailureCount() + 1);
                }
            }
            userAccountEntity.setFailureTime(LocalDateTime.now());
            if (userAccountEntity.getFailureCount() >= failureMax && userAccountEntity.getRecordStatus() >= 0) {
                userAccountEntity.setRecordStatus(-1);
            }
            userAccountRepository.save(userAccountEntity);
        }
    }
}
