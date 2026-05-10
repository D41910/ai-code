package com.dsj.aicode.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.dsj.aicode.Exception.BusinessException;
import com.dsj.aicode.Exception.ErrorCode;
import com.dsj.aicode.Exception.ThrowUtils;
import com.dsj.aicode.constant.UserConstant;
import com.dsj.aicode.mapper.UserMapper;
import com.dsj.aicode.model.dto.UserQueryDTO;
import com.dsj.aicode.model.entity.User;
import com.dsj.aicode.model.enums.UserRoleEnum;
import com.dsj.aicode.model.vo.UserVO;
import com.dsj.aicode.service.UserService;
import com.dsj.aicode.utils.SnowFlakeUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户 服务层实现。
 *
 * @author dongsijun
 * @since 2026/04/01 15:16
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //校验
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不得小于4位");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不得小于8位");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        //检查是否有重复账户
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_account", userAccount);
        long count = this.mapper.selectCountByQuery(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        //加密
        String encryptPassword = SnowFlakeUtil.getEncryptPassword(userPassword);
        //插入数据
        User user = User.builder()
                .userAccount(userAccount)
                .userPassword(encryptPassword)
                .userName("无名")
                .userRole(UserRoleEnum.USER.getValue())
                .build();
        boolean save = this.save(user);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败,数据库错误");
        }
        return user.getId();
    }

    @Override
    public UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户不得小于四位");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不得小于8位");
        }
        String encryptPassword = SnowFlakeUtil.getEncryptPassword(userPassword);
        //查询用户是否存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_password", encryptPassword);
        User user = this.mapper.selectOneByQuery(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        //记录用户登陆状态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user.getId());
        return getUserVO(user);
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        //判断是否登陆
        Object userId = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long currentUserId = (Long) userId;
        if(currentUserId == null || currentUserId <= 0){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //从数据看库查询
        User currentUser = this.getById(currentUserId);
        if(currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //移除登陆状态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }


    @Override
    public User getLoginUser(HttpServletRequest request) {
        //判断是否登陆
        Object userId = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        Long currentUserId = (Long) userId;
        if(currentUserId == null || currentUserId <= 0){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //从数据看库查询
        User currentUser = this.getById(currentUserId);
        if(currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 获取脱敏的已登陆用户信息
     *
     * @param user 用户信息
     * @return 脱敏后用户信息
     */
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();

        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public Page<UserVO> listUserVOByPage(UserQueryDTO userQueryDTO) {
        QueryWrapper queryWrapper = new QueryWrapper().eq("id", userQueryDTO.getId())
                .eq("user_role", userQueryDTO.getUserRole())
                .like("user_name", userQueryDTO.getUserName(),StrUtil.isNotEmpty(userQueryDTO.getUserName()))
                .like("user_account", userQueryDTO.getUserAccount(),StrUtil.isNotEmpty(userQueryDTO.getUserAccount()))
                .like("user_profile", userQueryDTO.getUserProfile(),StrUtil.isNotEmpty(userQueryDTO.getUserProfile()))
                .ge( "create_time", userQueryDTO.getStartTime(),userQueryDTO.getStartTime() != null)
                .le("create_time", userQueryDTO.getEndTime(),userQueryDTO.getEndTime() != null)
                .orderBy(userQueryDTO.getSortField(), "asc".equals(userQueryDTO.getSortOrder()));
        Page<User> pageUsers = this.page(Page.of(userQueryDTO.getPageNum(), userQueryDTO.getPageSize()), queryWrapper);
        List<UserVO> userVOList = this.getUserVOList(pageUsers.getRecords());
        Page<UserVO> page = new Page<>(pageUsers.getPageNumber(), pageUsers.getPageSize(), pageUsers.getTotalRow());
        page.setRecords(userVOList);
        return page;

    }

    @Override
    public boolean deleteUser(Long id) {
        User user = this.getById(id);
        ThrowUtils.throwIf(ObjUtil.isEmpty(user),ErrorCode.NOT_FOUND_ERROR);
        user.setIsDelete(1);
        return this.updateById(user);
    }

    @Override
    public List<UserVO> getUserVOList(List<User> users) {
        if(CollUtil.isEmpty(users)){
            return new ArrayList<>();
        }
        return users.stream().map(this::getUserVO).collect(Collectors.toList());
    }


}
