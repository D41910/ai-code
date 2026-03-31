package com.dsj.aicode.common;

import lombok.Data;

/**
 * @author dongsijun
 * @date 2026/3/31  16:58
 */
@Data
public class PageRequest {
    /**
     * 当前页号
     */
    private int pageNum = 1;

    /**
     * 页面大小
     */
    private int paseSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序
     */
    private String sortOrder = "descend";
}
