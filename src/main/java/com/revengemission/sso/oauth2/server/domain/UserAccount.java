package com.revengemission.sso.oauth2.server.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class UserAccount extends BaseDomain {
    /**
     *
     */
    private static final long serialVersionUID = -2355580690719376576L;
    private String clientId;
    private String username;
    private String password;
    private String nickName;
    private String avatarUrl;
    private String email;
    private String mobile;
    private String province;
    private String city;
    private String address;
    private Date birthday;
    private String gender;
    private Date failureTime;
    private int failureCount;
    private List<Role> roles = new ArrayList<>();
}
