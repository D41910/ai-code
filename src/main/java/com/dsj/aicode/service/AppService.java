package com.dsj.aicode.service;

import com.dsj.aicode.model.dto.AppAdminUpdateDTO;
import com.dsj.aicode.model.dto.AppQueryDTO;
import com.dsj.aicode.model.dto.AppUpdateDTO;
import com.dsj.aicode.model.dto.AppAddDTO;
import com.dsj.aicode.model.entity.App;
import com.dsj.aicode.model.entity.User;
import com.dsj.aicode.model.vo.AppVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author dongsijun
 * @since 2026/04/15 14:42
 */
public interface AppService extends IService<App> {


    /**
     * 应用聊天生成代码
     *
     * @param appId     应用ID
     * @param message   用户消息
     * @param loginUser 请求对象
     * @return 生成流式结果
     */
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser);

    /**
     * 创建应用
     *
     * @param appAddDTO 创建参数
     * @param request   Http请求
     * @return 应用id
     */
    long addApp(AppAddDTO appAddDTO, HttpServletRequest request);

    /**
     * 用户更新自己的应用（仅支持修改应用名称）
     *
     * @param appUpdateDTO 更新参数
     * @param request      Http请求
     * @return 是否成功
     */
    boolean updateOneself(AppUpdateDTO appUpdateDTO, HttpServletRequest request);

    /**
     * 用户删除自己的应用
     *
     * @param id      应用id
     * @param request Http请求
     * @return 是否成功
     */
    boolean deleteOneself(Long id, HttpServletRequest request);

    /**
     * 用户查看自己的应用详情
     *
     * @param id 应用id
     * @return 应用详情
     */
    AppVO getAppVOById(Long id);

    /**
     * 分页查询自己的应用列表
     *
     * @param appQueryDTO 查询参数
     * @param request     Http请求
     * @return 分页结果
     */
    Page<AppVO> listOneselfByPage(AppQueryDTO appQueryDTO, HttpServletRequest request);

    /**
     * 分页查询精选的应用列表
     *
     * @param appQueryDTO 查询参数
     * @return 分页结果
     */
    Page<AppVO> listFeaturedByPage(AppQueryDTO appQueryDTO);

    /**
     * 管理员删除任意应用
     *
     * @param id 应用id
     * @return 是否成功
     */
    boolean adminDelete(Long id);

    /**
     * 管理员更新任意应用
     *
     * @param appAdminUpdateDTO 更新参数
     * @return 是否成功
     */
    boolean adminUpdate(AppAdminUpdateDTO appAdminUpdateDTO);

    /**
     * 管理员分页查询应用列表
     *
     * @param appQueryDTO 查询参数
     * @return 分页结果
     */
    Page<AppVO> adminListByPage(AppQueryDTO appQueryDTO);

    /**
     * 管理员查看应用详情
     *
     * @param id 应用id
     * @return 应用详情
     */
    AppVO adminGetById(Long id);

    /**
     * 获取脱敏的应用信息
     *
     * @param app 应用信息
     * @return 脱敏后应用信息
     */
    AppVO getAppVO(App app);

    /**
     * 批量获取脱敏的应用信息
     *
     * @param apps 应用信息列表
     * @return 脱敏后应用信息列表
     */
    List<AppVO> getAppVOList(List<App> apps);

}
