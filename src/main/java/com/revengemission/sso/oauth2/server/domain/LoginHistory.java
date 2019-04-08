package com.revengemission.sso.oauth2.server.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class LoginHistory extends BaseDomain {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3503838536778480869L;
	private String clientId;
    private String username;
    private String ip;
    private String device;
}
