package com.dsj.aicode.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author dongsijun
 * @date 2026/4/7  14:18
 */
@Data
public class UserUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2050464796584190800L;

    /**
     * ID
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：0-user,1-admin
     */
    private Integer userRole;
}
