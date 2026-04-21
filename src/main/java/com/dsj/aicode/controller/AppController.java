package com.dsj.aicode.controller;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.dsj.aicode.Exception.ErrorCode;
import com.dsj.aicode.Exception.ThrowUtils;
import com.dsj.aicode.annotation.AuthCheck;
import com.dsj.aicode.common.BaseResponse;
import com.dsj.aicode.common.DeleteRequest;
import com.dsj.aicode.common.ResultUtils;
import com.dsj.aicode.constant.UserConstant;
import com.dsj.aicode.model.dto.AppAddDTO;
import com.dsj.aicode.model.dto.AppAdminUpdateDTO;
import com.dsj.aicode.model.dto.AppQueryDTO;
import com.dsj.aicode.model.dto.AppUpdateDTO;
import com.dsj.aicode.model.entity.App;
import com.dsj.aicode.model.entity.User;
import com.dsj.aicode.model.enums.CodeGenTypeEnum;
import com.dsj.aicode.model.vo.AppVO;
import com.dsj.aicode.service.AppService;
import com.dsj.aicode.service.UserService;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 应用 控制层。
 *
 * @author dongsijun
 * @since 2026/04/15 14:42
 */
@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;
    private final UserService userService;


    /**
     * 应用聊天生成代码
     *
     * @param appId   应用ID
     * @param message 用户消息
     * @param request 请求体
     * @return 生成流式结果
     */
    @GetMapping(value = "/chat/gen/code", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatToGenCode(@RequestParam Long appId, @RequestParam String message, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(appId) || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID无效");
        ThrowUtils.throwIf(StrUtil.isEmpty(message), ErrorCode.PARAMS_ERROR, "用户消息不能为空");
        //获取当前登陆用户
        User loginUser = userService.getLoginUser(request);
        Flux<String> flux = appService.chatToGenCode(appId, message, loginUser);
        return flux
                .map(chunk -> {
                    Map<String, String> map = Map.of("d", chunk);
                    String jsonStr = JSONUtil.toJsonStr(map);
                    return ServerSentEvent.<String>builder()
                            .data(jsonStr)
                            .build();
                })
                .concatWith(Mono.just(
                        ServerSentEvent.<String>builder()
                                .data("done")
                                .data("")
                                .build()
                ));
    }

    /**
     * 创建应用（须填写 initPrompt）
     *
     * @param appAddDTO 创建参数
     * @param request   Http请求
     * @return 应用id
     */
    @PostMapping("/add")
    public BaseResponse<Long> addApp(@RequestBody AppAddDTO appAddDTO, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(appAddDTO),ErrorCode.PARAMS_ERROR);
        String initPrompt = appAddDTO.getInitPrompt();
        ThrowUtils.throwIf(StrUtil.isBlank(initPrompt),ErrorCode.PARAMS_ERROR);
        long appId = appService.addApp(appAddDTO, request);
        return ResultUtils.success(appId);
    }

    /**
     * 用户更新自己的应用（目前只支持修改应用名称）
     *
     * @param appUpdateDTO 更新参数
     * @param request      Http请求
     * @return 是否成功
     */
    @PutMapping("/update/oneself")
    public BaseResponse<Boolean> updateOneself(@RequestBody AppUpdateDTO appUpdateDTO, HttpServletRequest request) {
        // 校验参数
        ThrowUtils.throwIf(ObjUtil.isEmpty(appUpdateDTO) || ObjUtil.isEmpty(appUpdateDTO.getId()) || appUpdateDTO.getId() <= 0, ErrorCode.PARAMS_ERROR);
        boolean result = appService.updateOneself(appUpdateDTO, request);
        return ResultUtils.success(result);
    }

    /**
     * 用户删除自己的应用
     *
     * @param deleteRequest 删除请求
     * @param request       Http请求
     * @return 是否成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteOneself(DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(deleteRequest) || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        boolean result = appService.deleteOneself(deleteRequest.getId(), request);
        return ResultUtils.success(result);
    }

    /**
     * 用户查看自己的应用详情
     *
     * @param id 应用id
     * @return 应用详情
     */
    @GetMapping("/get")
    public BaseResponse<AppVO> getAppVOById(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        AppVO appVO = appService.getAppVOById(id);
        return ResultUtils.success(appVO);
    }

    /**
     * 分页查询自己的应用列表（支持根据名称查询，每页最多20个）
     *
     * @param appQueryDTO 查询参数
     * @param request     Http请求
     * @return 分页结果
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<AppVO>> listOneselfByPage(@RequestBody AppQueryDTO appQueryDTO, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(appQueryDTO), ErrorCode.PARAMS_ERROR);
        if (appQueryDTO.getPageNum() <= 0) {
            appQueryDTO.setPageNum(1);
        }
        if (appQueryDTO.getPaseSize() <= 0) {
            appQueryDTO.setPaseSize(10);
        }
        Page<AppVO> page = appService.listOneselfByPage(appQueryDTO, request);
        return ResultUtils.success(page);
    }

    /**
     * 分页查询精选的应用列表（支持根据名称查询，每页最多20个）
     *
     * @param appQueryDTO 查询参数
     * @return 分页结果
     */
    @PostMapping("/list/page/featured")
    public BaseResponse<Page<AppVO>> listFeaturedByPage(@RequestBody AppQueryDTO appQueryDTO) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(appQueryDTO), ErrorCode.PARAMS_ERROR);
        if (appQueryDTO.getPageNum() <= 0) {
            appQueryDTO.setPageNum(1);
        }
        if (appQueryDTO.getPaseSize() <= 0) {
            appQueryDTO.setPaseSize(10);
        }
        Page<AppVO> page = appService.listFeaturedByPage(appQueryDTO);
        return ResultUtils.success(page);
    }

    // ==================== 管理员接口 ====================

    /**
     * 管理员删除任意应用
     *
     * @param deleteRequest 删除请求
     * @return 是否成功
     */
    @DeleteMapping("/admin/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> adminDelete(DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(deleteRequest) || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        boolean result = appService.adminDelete(deleteRequest.getId());
        return ResultUtils.success(result);
    }

    /**
     * 管理员更新任意应用（支持更新应用名称、应用封面、优先级）
     *
     * @param appAdminUpdateDTO 更新参数
     * @return 是否成功
     */
    @PutMapping("/admin/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> adminUpdate(@RequestBody AppAdminUpdateDTO appAdminUpdateDTO) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(appAdminUpdateDTO) || ObjUtil.isEmpty(appAdminUpdateDTO.getId()) || appAdminUpdateDTO.getId() <= 0, ErrorCode.PARAMS_ERROR);
        boolean result = appService.adminUpdate(appAdminUpdateDTO);
        return ResultUtils.success(result);
    }

    /**
     * 管理员分页查询应用列表（支持根据除时间外的任何字段查询，每页数量不限）
     *
     * @param appQueryDTO 查询参数
     * @return 分页结果
     */
    @PostMapping("/admin/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<AppVO>> adminListByPage(@RequestBody AppQueryDTO appQueryDTO) {
        ThrowUtils.throwIf(ObjUtil.isEmpty(appQueryDTO), ErrorCode.PARAMS_ERROR);
        if (appQueryDTO.getPageNum() <= 0) {
            appQueryDTO.setPageNum(1);
        }
        if (appQueryDTO.getPaseSize() <= 0) {
            appQueryDTO.setPaseSize(10);
        }
        Page<AppVO> page = appService.adminListByPage(appQueryDTO);
        return ResultUtils.success(page);
    }

    /**
     * 管理员查看应用详情
     *
     * @param id 应用id
     * @return 应用详情
     */
    @GetMapping("/admin/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<AppVO> adminGetById(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        AppVO appVO = appService.adminGetById(id);
        return ResultUtils.success(appVO);
    }

}
