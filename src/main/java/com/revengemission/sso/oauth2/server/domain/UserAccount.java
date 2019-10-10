package com.revengemission.sso.oauth2.server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private String username;
    @JsonIgnore
    private String password;
    /**
     * 多种登陆方式合并账号使用
     */
    private String accountOpenCode;
    private String nickName;
    private String avatarUrl;
    private String email;
    private String mobile;
    private String province;
    private String city;
    private String address;
    private LocalDate birthday;
    private String gender;
    private LocalDateTime failureTime;
    private int failureCount;
    private List<Role> roles = new ArrayList<>();
}
