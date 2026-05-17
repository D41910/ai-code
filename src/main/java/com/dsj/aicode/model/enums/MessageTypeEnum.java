package com.dsj.aicode.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 消息类型枚举
 *
 * @author dongsijun
 * @date 2026/5/10
 */
@Getter
public enum MessageTypeEnum {

    SYSTEM(0, "系统消息", "system"),
    USER(1, "用户消息", "user"),
    AI(2, "AI消息", "ai");

    private final int code;
    private final String text;
    private final String value;

    MessageTypeEnum(int code, String text, String value) {
        this.code = code;
        this.text = text;
        this.value = value;
    }

    /**
     * 根据code获取枚举
     *
     * @param code 消息类型code
     * @return 枚举实例
     */
    public static MessageTypeEnum getEnumByCode(Integer code) {
        if (ObjUtil.isEmpty(code)) {
            return null;
        }
        for (MessageTypeEnum messageTypeEnum : MessageTypeEnum.values()) {
            if (messageTypeEnum.getCode() == code) {
                return messageTypeEnum;
            }
        }
        return null;
    }


    /**
     * 根据value获取枚举
     *
     * @param value 消息类型value
     * @return 枚举实例
     */
    public static MessageTypeEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (MessageTypeEnum messageTypeEnum : MessageTypeEnum.values()) {
            if (messageTypeEnum.getValue().equals(value)) {
                return messageTypeEnum;
            }
        }
        return null;
    }

}