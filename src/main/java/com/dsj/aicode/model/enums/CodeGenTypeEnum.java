package com.dsj.aicode.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * @author dongsijun
 * @date 2026/4/14  09:22
 */
@Getter
public enum CodeGenTypeEnum {
    HTML("原生HTML模式","html"),
    MULTI_FILE("原生多文件模式","multi_file");

    private final String text;

    private final String value;

    CodeGenTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value获取枚举
     * @param value
     * @return
     */
    public static CodeGenTypeEnum getEnumByValue(String value) {
        if(ObjUtil.isEmpty(value)){
            return null;
        }
        for (CodeGenTypeEnum codeGenTypeEnum : CodeGenTypeEnum.values()) {
            if(codeGenTypeEnum.getValue().equals(value)){
                return codeGenTypeEnum;
            }
        }
        return null;
    }
}
