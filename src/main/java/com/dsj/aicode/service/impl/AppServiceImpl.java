package com.dsj.aicode.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.dsj.aicode.Exception.BusinessException;
import com.dsj.aicode.Exception.ErrorCode;
import com.dsj.aicode.Exception.ThrowUtils;
import com.dsj.aicode.constant.AppConstant;
import com.dsj.aicode.mapper.AppMapper;
import com.dsj.aicode.mapper.UserMapper;
import com.dsj.aicode.model.dto.AppAddDTO;
import com.dsj.aicode.model.dto.AppAdminUpdateDTO;
import com.dsj.aicode.model.dto.AppQueryDTO;
import com.dsj.aicode.model.dto.AppUpdateDTO;
import com.dsj.aicode.model.entity.App;
import com.dsj.aicode.model.entity.User;
import com.dsj.aicode.model.enums.CodeGenTypeEnum;
import com.dsj.aicode.model.enums.UserRoleEnum;
import com.dsj.aicode.model.vo.AppVO;
import com.dsj.aicode.model.vo.UserVO;
import com.dsj.aicode.service.AppService;
import com.dsj.aicode.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author dongsijun
 * @since 2026/04/15 14:42
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    private final UserService userService;

    public AppServiceImpl(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    public long addApp(AppAddDTO appAddDTO, HttpServletRequest request) {
        String initPrompt = appAddDTO.getInitPrompt();
        // 校验参数
        User loginUser = userService.getLoginUser(request);

        App app = new App();
        BeanUtils.copyProperties(appAddDTO, app);
        app.setUserId(loginUser.getId());
        // todo 应用名称暂时为initPrompt前12位
        app.setAppName(initPrompt.substring(0, Math.min(initPrompt.length(), 12)));
        //todo 暂时设置为多文件生成
        app.setCodeGenType(CodeGenTypeEnum.MULTI_FILE.getValue());

        // 保存
        boolean save = this.save(app);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        return app.getId();
    }

    @Override
    public boolean updateOneself(AppUpdateDTO appUpdateDTO, HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 查询应用
        App app = this.getById(appUpdateDTO.getId());
        ThrowUtils.throwIf(ObjUtil.isEmpty(app), ErrorCode.NOT_FOUND_ERROR);

        // 校验权限：只能修改自己的应用
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR);

        // 更新应用名称
        if (StrUtil.isNotBlank(appUpdateDTO.getAppName())) {
            app.setAppName(appUpdateDTO.getAppName());
        }
        app.setEditTime(LocalDateTime.now());
        boolean result = this.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public boolean deleteOneself(Long id, HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 查询应用
        App app = this.getById(id);
        ThrowUtils.throwIf(ObjUtil.isEmpty(app), ErrorCode.NOT_FOUND_ERROR);

        // 校验权限：只能删除自己的应用
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR);

        // 逻辑删除
        app.setIsDelete(1);
        return this.updateById(app);
    }

    @Override
    public AppVO getAppVOById(Long id) {

        // 查询应用
        App app = this.getById(id);
        ThrowUtils.throwIf(ObjUtil.isEmpty(app), ErrorCode.NOT_FOUND_ERROR);

        return getAppVO(app);
    }

    @Override
    public Page<AppVO> listOneselfByPage(AppQueryDTO appQueryDTO, HttpServletRequest request) {
        // 校验参数
        ThrowUtils.throwIf(ObjUtil.isEmpty(appQueryDTO), ErrorCode.PARAMS_ERROR);

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        appQueryDTO.setUserId(loginUser.getId());

        // 分页查询，每页最多20个
        int pageSize = appQueryDTO.getPaseSize() <= 0 ? 10 : Math.min(appQueryDTO.getPaseSize(), 20);
        int pageNum = appQueryDTO.getPageNum() <= 0 ? 1 : appQueryDTO.getPageNum();

        QueryWrapper queryWrapper = getQueryWrapper(appQueryDTO);
        Page<App> pageApps = this.page(Page.of(pageNum, pageSize), queryWrapper);
        List<AppVO> appVOList = this.getAppVOList(pageApps.getRecords());

        Page<AppVO> page = new Page<>(pageApps.getPageNumber(), pageApps.getPageSize(), pageApps.getTotalRow());
        page.setRecords(appVOList);
        return page;
    }

    @Override
    public Page<AppVO> listFeaturedByPage(AppQueryDTO appQueryDTO) {
        // 校验参数
        ThrowUtils.throwIf(ObjUtil.isEmpty(appQueryDTO), ErrorCode.PARAMS_ERROR);

        // 分页查询，每页最多20个
        int pageSize = appQueryDTO.getPaseSize() <= 0 ? 10 : Math.min(appQueryDTO.getPaseSize(), 20);
        int pageNum = appQueryDTO.getPageNum() <= 0 ? 1 : appQueryDTO.getPageNum();

        appQueryDTO.setPriority(AppConstant.GOOD_APP_PRIORITY);
        QueryWrapper queryWrapper = getQueryWrapper(appQueryDTO);

        Page<App> pageApps = this.page(Page.of(pageNum, pageSize), queryWrapper);

        List<AppVO> appVOList = this.getAppVOList(pageApps.getRecords());
        Page<AppVO> page = new Page<>(pageApps.getPageNumber(), pageApps.getPageSize(), pageApps.getTotalRow());
        page.setRecords(appVOList);
        return page;

    }

    @Override
    public boolean adminDelete(Long id) {
        // 校验参数
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);

        // 查询应用
        App app = this.getById(id);
        ThrowUtils.throwIf(ObjUtil.isEmpty(app), ErrorCode.NOT_FOUND_ERROR);

        // 逻辑删除
        app.setIsDelete(1);
        return this.updateById(app);
    }

    @Override
    public boolean adminUpdate(AppAdminUpdateDTO appAdminUpdateDTO) {
        // 校验参数
        ThrowUtils.throwIf(ObjUtil.isEmpty(appAdminUpdateDTO) || ObjUtil.isEmpty(appAdminUpdateDTO.getId()) || appAdminUpdateDTO.getId() <= 0, ErrorCode.PARAMS_ERROR);

        // 查询应用
        App app = this.getById(appAdminUpdateDTO.getId());
        ThrowUtils.throwIf(ObjUtil.isEmpty(app), ErrorCode.NOT_FOUND_ERROR);

        // 更新字段
        if (StrUtil.isNotBlank(appAdminUpdateDTO.getAppName())) {
            app.setAppName(appAdminUpdateDTO.getAppName());
        }
        if (StrUtil.isNotBlank(appAdminUpdateDTO.getCover())) {
            app.setCover(appAdminUpdateDTO.getCover());
        }
        if (appAdminUpdateDTO.getPriority() != null) {
            app.setPriority(appAdminUpdateDTO.getPriority());
        }
        app.setEditTime(LocalDateTime.now());
        return this.updateById(app);
    }

    @Override
    public Page<AppVO> adminListByPage(AppQueryDTO appQueryDTO) {
        // 校验参数
        ThrowUtils.throwIf(ObjUtil.isEmpty(appQueryDTO), ErrorCode.PARAMS_ERROR);

        // 构建查询条件
        QueryWrapper queryWrapper = getQueryWrapper(appQueryDTO);
        // 分页查询，pageSize为0或负数时不限制
        int pageNum = appQueryDTO.getPageNum() <= 0 ? 1 : appQueryDTO.getPageNum();
        int pageSize = appQueryDTO.getPaseSize() <= 0 ? 10 : appQueryDTO.getPaseSize();

        Page<App> pageApps = this.page(Page.of(pageNum, pageSize), queryWrapper);
        List<AppVO> appVOList = this.getAppVOList(pageApps.getRecords());

        Page<AppVO> page = new Page<>(pageApps.getPageNumber(), pageApps.getPageSize(), pageApps.getTotalRow());
        page.setRecords(appVOList);
        return page;
    }

    @Override
    public AppVO adminGetById(Long id) {
        // 校验参数
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);

        // 查询应用
        App app = this.getById(id);
        ThrowUtils.throwIf(ObjUtil.isEmpty(app), ErrorCode.NOT_FOUND_ERROR);

        return getAppVO(app);
    }

    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        // 关联查询用户信息
        Long userId = app.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            appVO.setUser(userVO);
        }
        return appVO;
    }


    @Override
    public List<AppVO> getAppVOList(List<App> apps) {
        if (CollUtil.isEmpty(apps)) {
            return new ArrayList<>();
        }
        Set<Long> userIds = apps.stream()
                .map(App::getUserId)
                .collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVO));
        return apps.stream().map(app ->{
            AppVO appVO = getAppVO(app);
            UserVO userVO = userVOMap.get(app.getUserId());
            appVO.setUser(userVO);
            return appVO;
        }).collect(Collectors.toList());
    }

    private QueryWrapper getQueryWrapper(AppQueryDTO appQueryDTO) {
        if (appQueryDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryDTO.getId();
        String appName = appQueryDTO.getAppName();
        String cover = appQueryDTO.getCover();
        String initPrompt = appQueryDTO.getInitPrompt();
        String codeGenType = appQueryDTO.getCodeGenType();
        String deployKey = appQueryDTO.getDeployKey();
        Integer priority = appQueryDTO.getPriority();
        Long userId = appQueryDTO.getUserId();
        String sortField = appQueryDTO.getSortField();
        String sortOrder = appQueryDTO.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id, ObjUtil.isNotEmpty(appQueryDTO.getId()))
                .like("appName", appName, StrUtil.isNotBlank(appQueryDTO.getAppName()))
                .like("cover", cover, StrUtil.isNotBlank(appQueryDTO.getCover()))
                .like("initPrompt", initPrompt, StrUtil.isNotBlank(appQueryDTO.getInitPrompt()))
                .eq("codeGenType", codeGenType, StrUtil.isNotBlank(appQueryDTO.getCodeGenType()))
                .eq("deployKey", deployKey, StrUtil.isNotBlank(appQueryDTO.getDeployKey()))
                .eq("priority", priority, ObjUtil.isNotEmpty(appQueryDTO.getPriority()))
                .eq("userId", userId, ObjUtil.isNotEmpty(appQueryDTO.getUserId()))
                .orderBy(sortField, "ascend".equals(sortOrder));
    }


}
