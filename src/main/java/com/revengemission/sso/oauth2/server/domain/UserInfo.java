package com.revengemission.sso.oauth2.server.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UserInfo extends User {
    private static final long serialVersionUID = -1682227070901462452L;
    private String userId;
    private String nickname;

    public UserInfo(String userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this(userId, username, password, true, true, true, true, authorities);
    }

    public UserInfo(String userId, String username, String password, boolean enabled,
                    boolean accountNonExpired, boolean credentialsNonExpired,
                    boolean accountNonLocked,
                    Collection<? extends GrantedAuthority> authorities)
            throws IllegalArgumentException {
        super(username, password, enabled, accountNonExpired,
                credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}
