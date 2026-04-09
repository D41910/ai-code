package com.dsj.aicode.model.dto;

import com.dsj.aicode.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author dongsijun
 * @date 2026/4/7  14:20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryDTO extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 2566230107557397659L;

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色：0-user 1-admin ban
     */
    private Integer userRole;

    /**
     * 创建时间开始
     */
    private LocalDateTime startTime;

    /**
     * 创建时间结束
     */
    private LocalDateTime endTime;

}
