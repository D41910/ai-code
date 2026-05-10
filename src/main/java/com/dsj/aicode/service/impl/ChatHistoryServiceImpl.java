package com.dsj.aicode.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.dsj.aicode.model.entity.ChatHistory;
import com.dsj.aicode.mapper.ChatHistoryMapper;
import com.dsj.aicode.service.ChatHistoryService;
import org.springframework.stereotype.Service;

/**
 * 对话历史 服务层实现。
 *
 * @author dongsijun
 * @since 2026/05/10 15:13
 */
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory>  implements ChatHistoryService{

}
