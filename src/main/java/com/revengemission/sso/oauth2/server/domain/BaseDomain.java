
package com.revengemission.sso.oauth2.server.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class BaseDomain implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String id;
    /**
     * 创建时间
     */
    private Date dateCreated;
    /**
     * 修改时间
     */
    private Date lastModified;
    private Integer recordStatus;
    /**
     * 更改次数/每次修改+1
     */
    private Integer version;
    private String remarks;
    private String additionalData;
}
