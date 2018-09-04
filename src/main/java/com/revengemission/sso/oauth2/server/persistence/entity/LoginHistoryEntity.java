package com.revengemission.sso.oauth2.server.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


@Entity
@Table(indexes = {@Index(name = "index_username", columnList = "username")})
public class LoginHistoryEntity extends BaseEntity {

    //用于记录用户在哪个子系统进行的登陆
    private String clientId;
    @Column(nullable = false, columnDefinition = "VARCHAR(40)")
    private String username;
    private String ip;
    private String device;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
