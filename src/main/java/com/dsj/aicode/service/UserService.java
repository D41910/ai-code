package com.dsj.aicode.service;

import com.dsj.aicode.model.dto.UserQueryDTO;
import com.dsj.aicode.model.vo.UserVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.dsj.aicode.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

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

    /**
     * 获取登陆用户
     *
     * @param request Http请求
     * @return 脱敏后的用户信息
     */
    User getLoginUser(HttpServletRequest request);


    /**
     * 获取脱敏的用户信息
     *
     * @param users 用户信息
     * @return 脱敏后用户信息
     */
    List<UserVO> getUserVOList(List<User> users);

    /**
     * 用户登陆
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request      Http请求
     * @return 脱敏后的用户信息
     */
    UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 退出登陆
     *
     * @param request Http请求
     * @return 返回结果
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏的用户信息
     *
     * @param user 用户信息
     * @return 脱敏后用户信息
     */
    UserVO getUserVO(User user);

    /**
     * 条件分页查询
     * @param userQueryDTO 请求参数
     * @return 返回
     */
    Page<UserVO> listUserVOByPage(UserQueryDTO userQueryDTO);
}
