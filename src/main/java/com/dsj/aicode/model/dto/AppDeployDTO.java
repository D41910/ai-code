package com.dsj.aicode.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author dongsijun
 * @date 2026/4/21  15:19
 */
@Data
public class AppDeployDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 5405757281585216972L;

    /**
     * 应用Id
     */
    private Long appId;
}
