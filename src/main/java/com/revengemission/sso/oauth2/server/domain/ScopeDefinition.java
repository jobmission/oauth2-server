package com.revengemission.sso.oauth2.server.domain;

public class ScopeDefinition extends BaseDomain {
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
