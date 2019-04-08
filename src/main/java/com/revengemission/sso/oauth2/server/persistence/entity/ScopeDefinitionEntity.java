package com.revengemission.sso.oauth2.server.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"scope"}))
public class ScopeDefinitionEntity extends BaseEntity {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1522239249392557103L;
	private String scope;
    /*定义 解释*/
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
