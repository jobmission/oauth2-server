package com.revengemission.sso.oauth2.server.service.impl;

import com.revengemission.sso.oauth2.server.domain.AlreadyExistsException;
import com.revengemission.sso.oauth2.server.domain.EntityNotFoundException;
import com.revengemission.sso.oauth2.server.domain.JsonObjects;
import com.revengemission.sso.oauth2.server.domain.OauthClient;
import com.revengemission.sso.oauth2.server.mapper.OauthClientMapper;
import com.revengemission.sso.oauth2.server.persistence.entity.OauthClientEntity;
import com.revengemission.sso.oauth2.server.persistence.repository.OauthClientRepository;
import com.revengemission.sso.oauth2.server.service.OauthClientService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OauthClientServiceImpl implements OauthClientService {

    @Autowired
    OauthClientRepository oauthClientRepository;

    @Autowired
    OauthClientMapper mapper;

    @Override
    public OauthClient findByClientId(String clientId) {
        OauthClientEntity oauthClientEntity = oauthClientRepository.findByClientId(clientId);
        if (oauthClientEntity != null) {
            return mapper.entityToDto(oauthClientEntity);
        } else {
            return null;
        }
    }

    @Override
    public JsonObjects<OauthClient> list(int pageNum, int pageSize, String sortField, String sortOrder) {
        JsonObjects<OauthClient> jsonObjects = new JsonObjects<>();
        Sort sort;
        if (StringUtils.equalsIgnoreCase(sortOrder, "asc")) {
            sort = Sort.by(Sort.Direction.ASC, sortField);
        } else {
            sort = Sort.by(Sort.Direction.DESC, sortField);
        }
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<OauthClientEntity> page = oauthClientRepository.findAll(pageable);
        if (page.getContent() != null && page.getContent().size() > 0) {
            jsonObjects.setTotal(page.getTotalElements());
            jsonObjects.setPages(page.getTotalPages());
            page.getContent().forEach(u -> jsonObjects.getRows().add(mapper.entityToDto(u)));
        }
        return jsonObjects;
    }

    @Override
    public OauthClient create(OauthClient oauthClient) throws AlreadyExistsException {
        OauthClientEntity exist = oauthClientRepository.findByClientId(oauthClient.getClientId());
        if (exist != null) {
            throw new AlreadyExistsException(oauthClient.getClientId() + " already exists!");
        }
        OauthClientEntity oauthClientEntity = mapper.dtoToEntity(oauthClient);
        oauthClientRepository.save(oauthClientEntity);
        return mapper.entityToDto(oauthClientEntity);
    }

    @Override
    public OauthClient retrieveById(long id) throws EntityNotFoundException {
        Optional<OauthClientEntity> entityOptional = oauthClientRepository.findById(id);
        return mapper.entityToDto(entityOptional.orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public OauthClient updateById(OauthClient oauthClient) throws EntityNotFoundException {
        Optional<OauthClientEntity> entityOptional = oauthClientRepository.findById(Long.parseLong(oauthClient.getId()));
        OauthClientEntity e = entityOptional.orElseThrow(EntityNotFoundException::new);
        if (StringUtils.isNotEmpty(oauthClient.getClientSecret())) {
            e.setClientSecret(oauthClient.getClientSecret());
        }
        e.setAuthorities(oauthClient.getAuthorities());
        e.setScope(oauthClient.getScope());
        e.setAuthorizedGrantTypes(oauthClient.getAuthorizedGrantTypes());
        e.setWebServerRedirectUri(oauthClient.getWebServerRedirectUri());

        if (StringUtils.isNotEmpty(oauthClient.getRemarks())) {
            e.setRemarks(oauthClient.getRemarks());
        }

        oauthClientRepository.save(e);
        return mapper.entityToDto(e);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRecordStatus(long id, int recordStatus) {
        Optional<OauthClientEntity> entityOptional = oauthClientRepository.findById(id);
        OauthClientEntity e = entityOptional.orElseThrow(EntityNotFoundException::new);
        e.setRecordStatus(recordStatus);
        oauthClientRepository.save(e);
    }
}
