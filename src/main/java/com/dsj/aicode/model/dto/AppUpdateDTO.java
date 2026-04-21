package com.dsj.aicode.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 应用更新DTO（用户更新自己的应用）
 *
 * @author dongsijun
 * @date 2026/4/15  14:42
 */
@Data
public class AppUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2050464796584190801L;

    /**
     * ID
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

}
