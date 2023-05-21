package com.revengemission.sso.oauth2.server.mapper;

import com.revengemission.sso.oauth2.server.domain.LoginHistory;
import com.revengemission.sso.oauth2.server.persistence.entity.LoginHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface LoginHistoryMapper {
    LoginHistoryMapper INSTANCE = Mappers.getMapper(LoginHistoryMapper.class);

    @Mapping(target = "username", source = "username")
    LoginHistory entityToDto(LoginHistoryEntity entity);

    @Mapping(target = "username", source = "username")
    LoginHistoryEntity dtoToEntity(LoginHistory dto);
}
