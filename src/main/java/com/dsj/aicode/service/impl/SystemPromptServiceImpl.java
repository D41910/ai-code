package com.dsj.aicode.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.dsj.aicode.exception.ErrorCode;
import com.dsj.aicode.exception.ThrowUtils;
import com.dsj.aicode.mapper.AppMapper;
import com.dsj.aicode.model.entity.App;
import com.dsj.aicode.service.AppService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.dsj.aicode.model.entity.SystemPrompt;
import com.dsj.aicode.mapper.SystemPromptMapper;
import com.dsj.aicode.service.SystemPromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author dongsijun
 * @since 2026/05/17 17:25
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class SystemPromptServiceImpl extends ServiceImpl<SystemPromptMapper, SystemPrompt>  implements SystemPromptService{

    private final AppService appService;

    private final AppMapper appMapper;

    @Override
    public String getSystemPromptByAppId(Long appId) {
        ThrowUtils.throwIf(appId != null && appId <= 0, ErrorCode.PARAMS_ERROR,"AppId不能为空");
        App app = appService.getById(appId);
        ThrowUtils.throwIf(ObjectUtil.isEmpty(app),ErrorCode.NOT_FOUND_ERROR,"应用不存在");
        String systemPrompt = appMapper.getSystemPromptByAppId(appId);
        ThrowUtils.throwIf(StrUtil.isBlank(systemPrompt),ErrorCode.NOT_FOUND_ERROR,"不存在系统预设");
        return systemPrompt;
    }
}
