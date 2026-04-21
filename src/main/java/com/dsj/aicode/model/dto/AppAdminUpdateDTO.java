package com.dsj.aicode.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 应用管理更新DTO（管理员更新任意应用）
 *
 * @author dongsijun
 * @date 2026/4/15  14:42
 */
@Data
public class AppAdminUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3050464796584190802L;

    /**
     * ID
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用封面
     */
    private String cover;

    /**
     * 优先级
     */
    private Integer priority;

}
