package com.revengemission.sso.oauth2.server.mapper;

import com.revengemission.sso.oauth2.server.domain.Role;
import com.revengemission.sso.oauth2.server.persistence.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    @Mapping(target = "roleName", source = "roleName")
    Role entityToDto(RoleEntity entity);

    @Mapping(target = "roleName", source = "roleName")
    RoleEntity dtoToEntity(Role dto);
}
