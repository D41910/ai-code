package com.dsj.aicode.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.dsj.aicode.exception.ErrorCode;
import com.dsj.aicode.exception.ThrowUtils;
import com.dsj.aicode.mapper.ChatHistoryMapper;
import com.dsj.aicode.model.dto.ChatHistoryQueryDTO;
import com.dsj.aicode.model.entity.App;
import com.dsj.aicode.model.entity.ChatHistory;
import com.dsj.aicode.model.entity.User;
import com.dsj.aicode.model.enums.MessageTypeEnum;
import com.dsj.aicode.model.enums.UserRoleEnum;
import com.dsj.aicode.service.AppService;
import com.dsj.aicode.service.ChatHistoryService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 对话历史 服务层实现。
 *
 * @author dongsijun
 * @since 2026/05/10 15:13
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@Slf4j
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {

    private final AppService appService;

    @Override
    public int loadChatHistoryToMemory(Long appId, TokenWindowChatMemory chatMemory, int maxCount) {
        try {
            // ==============================================
            // 关键：这里取的就是【当前 appId 自己的对话】，绝对不会拿到别人的
            // ==============================================
            List<ChatMessage> existingMessages = chatMemory.messages();

            // 如果Redis里已经有历史了 → 直接返回，不查库、不覆盖
            if (CollUtil.isNotEmpty(existingMessages)) {
                log.info("appId:{} 对话已存在于Redis，跳过DB加载，数量:{}",
                        appId, existingMessages.size());
                return existingMessages.size();
            }

            // ==============================================
            // Redis 无数据 → 第一次加载，从DB读取
            // ==============================================
            QueryWrapper queryWrapper = QueryWrapper.create()
                    .eq(ChatHistory::getAppId, appId)
                    .orderBy(ChatHistory::getCreateTime, false)
                    .limit(1, maxCount);

            List<ChatHistory> historyList = this.list(queryWrapper);
            if (CollUtil.isEmpty(historyList)) {
                return 0;
            }

            // 反转顺序（老的在前）
            Collections.reverse(historyList);

            // 加入 chatMemory（自动写入 Redis，且按 appId 隔离）
            int loadedCount = 0;
            for (ChatHistory history : historyList) {
                if (history.getMessageType().equals(MessageTypeEnum.USER.getCode())) {
                    chatMemory.add(UserMessage.from(history.getMessage()));
                    loadedCount++;
                } else if (history.getMessageType().equals(MessageTypeEnum.AI.getCode())) {
                    chatMemory.add(AiMessage.from(history.getMessage()));
                    loadedCount++;
                }else if(history.getMessageType().equals(MessageTypeEnum.SYSTEM.getCode())){
                    chatMemory.add(SystemMessage.from(history.getMessage()));
                    loadedCount++;
                }
            }

            log.info("appId:{} 首次从DB加载对话成功，共{}条", appId, loadedCount);
            return loadedCount;

        } catch (Exception e) {
            log.error("加载对话历史失败 appId:{}", appId, e);
            return 0;
        }
    }

    @Override
    public boolean addChatMessage(Long appId, String message, String messageType, Long userId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用消息ID不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "消息内容不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(messageType), ErrorCode.PARAMS_ERROR, "消息类型不能为空");
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        //验证消息类型是否有效
        MessageTypeEnum messageTypeEnum = MessageTypeEnum.getEnumByValue(messageType);
        ThrowUtils.throwIf(messageTypeEnum == null, ErrorCode.PARAMS_ERROR, "不支持的消息类型: " + messageType);
        ChatHistory chatHistory = ChatHistory.builder()
                .appId(appId)
                .message(message)
                .messageType(messageTypeEnum.getCode())
                .userId(userId)
                .build();
        return this.save(chatHistory);
    }

    @Override
    public boolean deleteByAppId(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("app_id", appId);
        return this.remove(queryWrapper);
    }

    /**
     * 获取查询包装类
     *
     * @param chatHistoryQueryDTO
     * @return
     */
    @Override
    public QueryWrapper getQueryWrapper(ChatHistoryQueryDTO chatHistoryQueryDTO) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (chatHistoryQueryDTO == null) {
            return queryWrapper;
        }
        Long id = chatHistoryQueryDTO.getId();
        String message = chatHistoryQueryDTO.getMessage();
        Integer messageType = chatHistoryQueryDTO.getMessageType();
        Long appId = chatHistoryQueryDTO.getAppId();
        Long userId = chatHistoryQueryDTO.getUserId();
        LocalDateTime lastCreateTime = chatHistoryQueryDTO.getLastCreateTime();
        String sortField = chatHistoryQueryDTO.getSortField();
        String sortOrder = chatHistoryQueryDTO.getSortOrder();
        // 拼接查询条件
        queryWrapper.eq("id", id)
                .like("message", message)
                .eq("message_type", messageType)
                .eq("app_id", appId)
                .eq("user_id", userId);
        // 游标查询逻辑 - 只使用 createTime 作为游标
        if (lastCreateTime != null) {
            queryWrapper.lt("create_time", lastCreateTime);
        }
        // 排序
        if (StrUtil.isNotBlank(sortField)) {
            queryWrapper.orderBy(sortField, "ascend".equals(sortOrder));
        } else {
            // 默认按创建时间降序排列
            queryWrapper.orderBy("create_time", false);
        }
        return queryWrapper;
    }

    @Override
    public Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize, LocalDateTime lastCreateTime, User loginUser) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        ThrowUtils.throwIf(pageSize <= 0 || pageSize >= 50, ErrorCode.PARAMS_ERROR, "页面大小必须在1-50之间");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        //权限校验
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        boolean isAdmin = loginUser.getUserRole().equals(UserRoleEnum.ADMIN.getValue());
        boolean isCreator = app.getUserId().equals(loginUser.getId());
        ThrowUtils.throwIf(!isAdmin && !isCreator, ErrorCode.NO_AUTH_ERROR, "无权限查看该应用的对话历史");
        //构架查询条件
        ChatHistoryQueryDTO chatHistoryQueryDTO = new ChatHistoryQueryDTO();
        chatHistoryQueryDTO.setAppId(appId);
        chatHistoryQueryDTO.setLastCreateTime(lastCreateTime);
        QueryWrapper queryWrapper = this.getQueryWrapper(chatHistoryQueryDTO);
        return this.page(Page.of(1, pageSize), queryWrapper);
    }
}