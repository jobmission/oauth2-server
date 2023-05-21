package com.revengemission.sso.oauth2.server.mapper;

import com.revengemission.sso.oauth2.server.domain.ScopeDefinition;
import com.revengemission.sso.oauth2.server.persistence.entity.ScopeDefinitionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ScopeDefinitionMapper {
    ScopeDefinitionMapper INSTANCE = Mappers.getMapper(ScopeDefinitionMapper.class);

    @Mapping(target = "scope", source = "scope")
    ScopeDefinition entityToDto(ScopeDefinitionEntity entity);

    @Mapping(target = "scope", source = "scope")
    ScopeDefinitionEntity dtoToEntity(ScopeDefinition dto);
}
