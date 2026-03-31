package com.dsj.aicode.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author dongsijun
 * @date 2026/3/31  17:00
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;

}
