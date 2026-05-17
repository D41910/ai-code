package com.dsj.aicode.model.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  实体类。
 *
 * @author dongsijun
 * @since 2026/05/17 17:25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("system_prompt")
public class SystemPrompt implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 预设类型：html/multi-file
     */
    private String promptType;

    /**
     * 系统预设内容
     */
    private String content;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
