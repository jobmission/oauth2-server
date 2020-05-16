package com.revengemission.sso.oauth2.server.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class OauthClient extends BaseDomain {
    private static final long serialVersionUID = 8626957691648832578L;
    private String clientId;
    private String applicationName;
    private String resourceIds;
    private String clientSecret;
    private String scope;
    private String authorizedGrantTypes;
    private String webServerRedirectUri;
    private String authorities;
    private int accessTokenValidity = 60 * 60 * 2;
    private int refreshTokenValidity = 60 * 60 * 24;
    private String additionalInformation;
    private String autoApprove;
    /**
     * 客户端过期时间
     */
    private LocalDateTime expirationDate;
}
