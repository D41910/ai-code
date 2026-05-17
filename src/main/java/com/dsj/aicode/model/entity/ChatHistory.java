package com.dsj.aicode.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 对话历史 实体类。
 *
 * @author dongsijun
 * @since 2026/05/10 15:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("chat_history")
public class ChatHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /**
     * 消息
     */
    private String message;

    /**
     * 应用id
     */
    @Column("app_id")
    private Long appId;

    /**
     * 创建用户id
     */
    @Column("user_id")
    private Long userId;

    /**
     * 消息类型：1-用户消息，2-AI消息
     */
    @Column("message_type")
    private Integer messageType;

    /**
     * 创建时间
     */
    @Column("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @Column(value = "is_delete", isLogicDelete = true)
    private Integer isDelete;
}
