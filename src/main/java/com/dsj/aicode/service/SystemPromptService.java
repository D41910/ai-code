package com.dsj.aicode.service;

import com.mybatisflex.core.service.IService;
import com.dsj.aicode.model.entity.SystemPrompt;

/**
 *  服务层。
 *
 * @author dongsijun
 * @since 2026/05/17 17:25
 */
public interface SystemPromptService extends IService<SystemPrompt> {

    String getSystemPromptByAppId(Long appId);

}
