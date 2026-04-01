package com.dsj.aicode.service;

import com.mybatisflex.core.service.IService;
import com.dsj.aicode.model.entity.User;

/**
 * 用户 服务层。
 *
 * @author dongsijun
 * @since 2026/04/01 15:16
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 确认密码
     * @return 用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);
}
