package com.revengemission.sso.oauth2.server.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UserInfo extends User {
    private static final long serialVersionUID = -1682227070901462452L;
    private String accountOpenCode;
    private String nickname;

    public UserInfo(String accountOpenCode, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this(accountOpenCode, username, password, true, true, true, true, authorities);
    }

    public UserInfo(String accountOpenCode, String username, String password, boolean enabled,
                    boolean accountNonExpired, boolean credentialsNonExpired,
                    boolean accountNonLocked,
                    Collection<? extends GrantedAuthority> authorities)
            throws IllegalArgumentException {
        super(username, password, enabled, accountNonExpired,
                credentialsNonExpired, accountNonLocked, authorities);
        this.accountOpenCode = accountOpenCode;
    }

    public String getAccountOpenCode() {
        return accountOpenCode;
    }

    public void setAccountOpenCode(String accountOpenCode) {
        this.accountOpenCode = accountOpenCode;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}
