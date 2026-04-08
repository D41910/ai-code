package com.dsj.aicode.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author dongsijun
 * @date 2026/4/1  17:05
 */
@Data
public class UserLoginDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 221115140665599320L;

    /**
     * 账户
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;
}
