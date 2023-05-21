package com.revengemission.sso.oauth2.server.mapper;

import com.revengemission.sso.oauth2.server.domain.OauthClient;
import com.revengemission.sso.oauth2.server.persistence.entity.OauthClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OauthClientMapper {
    OauthClientMapper INSTANCE = Mappers.getMapper(OauthClientMapper.class);

    @Mapping(target = "clientId", source = "clientId")
    OauthClient entityToDto(OauthClientEntity entity);

    @Mapping(target = "clientId", source = "clientId")
    OauthClientEntity dtoToEntity(OauthClient dto);
}
