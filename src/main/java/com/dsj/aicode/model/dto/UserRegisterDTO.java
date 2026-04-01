package com.dsj.aicode.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author dongsijun
 * @date 2026/4/1  15:46
 */
@Data
public class UserRegisterDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5476502505418489882L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;
}
