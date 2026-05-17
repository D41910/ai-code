package com.dsj.aicode.service;

import com.dsj.aicode.model.dto.ChatHistoryQueryDTO;
import com.dsj.aicode.model.entity.ChatHistory;
import com.dsj.aicode.model.entity.User;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author dongsijun
 * @since 2026/05/10 15:13
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    int loadChatHistoryToMemory(Long appId, TokenWindowChatMemory chatMemory, int maxCount);

    /**
     * 保存对话消息
     *
     * @param appId       应用id
     * @param message     消息内容
     * @param messageType 消息类型
     * @param userId      用户id
     * @return 是否成功
     */
    boolean addChatMessage(Long appId, String message, String messageType, Long userId);

    /**
     * 获取查询包装类
     *
     * @param chatHistoryQueryDTO 分页查询参数
     * @return
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryDTO chatHistoryQueryDTO);

    /**
     * 分页查询应用的对话历史（按时间降序）
     *
     * @param appId          应用ID
     * @param pageSize       消息条数
     * @param lastCreateTime 最后一条消息创建时间
     * @param loginUser      登陆用户
     * @return 返回数据
     */
    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize, LocalDateTime lastCreateTime, User loginUser);

    /**
     * 根据应用id删除对话历史
     *
     * @param appId 应用id
     * @return 是否成功
     */
    boolean deleteByAppId(Long appId);
}