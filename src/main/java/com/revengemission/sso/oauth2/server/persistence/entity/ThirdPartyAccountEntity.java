package com.revengemission.sso.oauth2.server.persistence.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"thirdParty", "thirdPartyAccountId"}))
public class ThirdPartyAccountEntity extends BaseEntity {

    /**
     *
     */
    private static final long serialVersionUID = -5519234457588411587L;
    //用于记录用户在哪个子系统进行的注册
    @Column(nullable = false, columnDefinition = "VARCHAR(32)")
    private String clientId;
    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private String thirdParty;
    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    private String thirdPartyAccountId;
    //多种登陆方式合并账号使用
    private String accountOpenCode;
    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "third_party_account_id", referencedColumnName = "id"), foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"), inverseForeignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    List<RoleEntity> roles = new ArrayList<>();


    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getThirdParty() {
        return thirdParty;
    }

    public void setThirdParty(String thirdParty) {
        this.thirdParty = thirdParty;
    }

    public String getThirdPartyAccountId() {
        return thirdPartyAccountId;
    }

    public void setThirdPartyAccountId(String thirdPartyAccountId) {
        this.thirdPartyAccountId = thirdPartyAccountId;
    }

    public String getAccountOpenCode() {
        return accountOpenCode;
    }

    public void setAccountOpenCode(String accountOpenCode) {
        this.accountOpenCode = accountOpenCode;
    }

    public List<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleEntity> roles) {
        this.roles = roles;
    }
}
