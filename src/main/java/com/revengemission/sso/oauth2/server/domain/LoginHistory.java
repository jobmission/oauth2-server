package com.revengemission.sso.oauth2.server.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class LoginHistory extends BaseDomain {
    private String clientId;
    private String username;
    private String ip;
    private String device;
}
