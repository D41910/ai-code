package com.dsj.aicode.model.dto;

import com.dsj.aicode.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 对话历史查询DTO
 *
 * @author dongsijun
 * @date 2026/5/10
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChatHistoryQueryDTO extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -8572632826431622092L;

    /**
     * id
     */
    private Long id;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 消息类型（user/ai）
     */
    private Integer messageType;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 创建用户id
     */
    private Long userId;

    /**
     * 游标查询 - 最后一条记录的创建时间
     * 用于分页查询，获取早于此时间的记录
     */
    private LocalDateTime lastCreateTime;

}
