package com.revengemission.sso.oauth2.server.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"scope"}))
public class ScopeDefinitionEntity extends BaseEntity {
    /**
     *
     */
    private static final long serialVersionUID = 1522239249392557103L;
    private String scope;
    /**
     * 定义 解释
     */
    private String definition;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
