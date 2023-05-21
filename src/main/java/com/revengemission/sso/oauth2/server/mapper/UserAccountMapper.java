package com.revengemission.sso.oauth2.server.mapper;

import com.revengemission.sso.oauth2.server.domain.UserAccount;
import com.revengemission.sso.oauth2.server.persistence.entity.UserAccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserAccountMapper {
    UserAccountMapper INSTANCE = Mappers.getMapper(UserAccountMapper.class);

    @Mapping(target = "username", source = "username")
    UserAccount entityToDto(UserAccountEntity entity);

    @Mapping(target = "username", source = "username")
    UserAccountEntity dtoToEntity(UserAccount dto);
}
