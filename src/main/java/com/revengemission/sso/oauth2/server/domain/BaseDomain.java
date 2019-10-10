
package com.revengemission.sso.oauth2.server.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class BaseDomain implements Serializable {

    private String id;
    /**
     * 创建时间
     */
    private LocalDateTime dateCreated;
    /**
     * 修改时间
     */
    private LocalDateTime lastModified;
    private int recordStatus;
    private int sortPriority;
    /**
     * 更改次数/每次修改+1
     */
    private int version;
    private String remarks;
    private String additionalData;
}
