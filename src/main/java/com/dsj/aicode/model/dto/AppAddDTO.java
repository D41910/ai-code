package com.dsj.aicode.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 应用创建DTO
 *
 * @author dongsijun
 * @date 2026/4/15  14:42
 */
@Data
public class AppAddDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5092286236899377934L;

    /**
     * 应用初始化的 prompt（必填）
     */
    private String initPrompt;

}
