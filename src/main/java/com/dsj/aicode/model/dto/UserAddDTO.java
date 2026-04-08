package com.dsj.aicode.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author dongsijun
 * @date 2026/4/7  14:16
 */
@Data
public class UserAddDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5092286236899377933L;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色:0-user,1-admin
     */
    private Integer userRole;

}
