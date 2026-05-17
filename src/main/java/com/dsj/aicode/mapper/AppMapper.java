package com.dsj.aicode.mapper;

import com.dsj.aicode.model.entity.App;
import com.mybatisflex.core.BaseMapper;

/**
 * 应用 映射层。
 *
 * @author dongsijun
 * @since 2026/04/15 14:42
 */
public interface AppMapper extends BaseMapper<App> {

    String getSystemPromptByAppId(Long appId);
}
