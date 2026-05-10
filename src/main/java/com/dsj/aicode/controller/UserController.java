package com.dsj.aicode.controller;

import cn.hutool.core.util.ObjUtil;
import com.dsj.aicode.Exception.ErrorCode;
import com.dsj.aicode.Exception.ThrowUtils;
import com.dsj.aicode.annotation.AuthCheck;
import com.dsj.aicode.common.BaseResponse;
import com.dsj.aicode.common.DeleteRequest;
import com.dsj.aicode.common.ResultUtils;
import com.dsj.aicode.constant.UserConstant;
import com.dsj.aicode.model.dto.*;
import com.dsj.aicode.model.entity.User;
import com.dsj.aicode.model.vo.UserVO;
import com.dsj.aicode.service.UserService;
import com.dsj.aicode.utils.SnowFlakeUtil;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户 控制层。
 *
 * @author dongsijun
 * @since 2026/04/01 15:16
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 退出登陆
     * @param request Http请求
     * @return 返回结果
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(request),ErrorCode.PARAMS_ERROR);
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取登陆用户
     * @param request Http请求
     * @return 脱敏后的用户信息
     */
    @GetMapping("/get/login")
    public BaseResponse<UserVO> getLoginUserVO(HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(request),ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getUserVO(loginUser));
    }

    /**
     * 用户登陆
     *
     * @param userLoginDTO 用户登陆对象
     * @param request      Http请求
     * @return 返回用户信息
     */
    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(userLoginDTO), ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginDTO.getUserAccount();
        String userPassword = userLoginDTO.getUserPassword();
        UserVO userVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(userVO);
    }


    /**
     * 用户注册
     *
     * @param userRegisterDTO 用户注册请求对象
     * @return 注册结果 用户id
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterDTO userRegisterDTO) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(userRegisterDTO), ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterDTO.getUserAccount();
        String userPassword = userRegisterDTO.getUserPassword();
        String checkPassword = userRegisterDTO.getCheckPassword();
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 保存用户。
     *
     * @param addDTO 用户
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddDTO addDTO) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(addDTO), ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtils.copyProperties(addDTO, user);
        final String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = SnowFlakeUtil.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 根据主键删除用户。
     *
     * @param deleteRequest 删除用户请求对象
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(deleteRequest) || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        boolean result = userService.deleteUser(deleteRequest.getId());
        return ResultUtils.success(result);
    }



    /**
     * 根据主键更新用户。用户更新自己信息
     *
     * @param userUpdateDTO 用户
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("/update/oneself")
    public BaseResponse<Boolean> updateUserOneself(@RequestBody UserUpdateDTO userUpdateDTO, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(userUpdateDTO) || ObjUtil.isEmpty(userUpdateDTO.getId()) || userUpdateDTO.getId() <= 0 , ErrorCode.PARAMS_ERROR);
        Long id = userService.getLoginUser(request).getId();
        ThrowUtils.throwIf(!id.equals(userUpdateDTO.getId()), ErrorCode.NO_AUTH_ERROR);
        User user = new User();
        BeanUtils.copyProperties(userUpdateDTO, user);
        user.setUserPassword(SnowFlakeUtil.getEncryptPassword(userUpdateDTO.getUserPassword()));
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }

    /**
     * 根据主键更新用户。
     *
     * @param userUpdateDTO 用户
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(userUpdateDTO) || ObjUtil.isEmpty(userUpdateDTO.getId()) || userUpdateDTO.getId() <= 0 , ErrorCode.PARAMS_ERROR);
        User user = new User();
        user.setUserPassword(SnowFlakeUtil.getEncryptPassword(userUpdateDTO.getUserPassword()));
        BeanUtils.copyProperties(userUpdateDTO, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }

    /**
     * 根据主键获取用户。
     *
     * @param id 用户主键
     * @return 用户详情
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(Long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(ObjUtil.isEmpty(user),ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }



    /**
     * 根据主键获取脱敏用户。
     *
     * @param id 用户主键
     * @return 脱敏后用户详情
     */
    @GetMapping("/get/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserVO> getUserVOById(Long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        UserVO userVO = userService.getUserVO(user);
        ThrowUtils.throwIf(ObjUtil.isEmpty(userVO),ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(userVO);
    }

    /**
     * 分页查询用户。
     *
     * @param userQueryDTO 分页对象
     * @return 分页对象
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryDTO userQueryDTO) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(userQueryDTO), ErrorCode.PARAMS_ERROR);
        if(userQueryDTO.getPageNum() <= 0){
            userQueryDTO.setPageNum(1);
        }
        if (userQueryDTO.getPageSize() <= 0){
            userQueryDTO.setPageSize(10);
        }
        return ResultUtils.success(userService.listUserVOByPage(userQueryDTO));
    }

}
