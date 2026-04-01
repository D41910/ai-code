package com.dsj.aicode.model.enums;

import lombok.Getter;

/**
 * @author dongsijun
 * @date 2026/4/1  14:56
 */
@Getter
public enum UserRoleEnum {

    /**
     * 普通用户：具有基本的使用权限
     */
    USER("普通用户", "user", 0),

    /**
     * 管理员用户：具有系统管理权限
     */
    ADMIN("管理员", "admin", 1);

    // 中文描述
    private final String description;
    // 英文代码/标识
    private final String code;
    // 数值索引（用于数据库存储）
    private final int value;

    // 构造函数
    UserRoleEnum(String description, String code, int value) {
        this.description = description;
        this.code = code;
        this.value = value;
    }

    /**
     * 根据数值索引获取枚举
     * @param value 数值
     * @return 对应的枚举对象，如果未找到则返回 null
     */
    public static UserRoleEnum getByValue(int value) {
        for (UserRoleEnum type : UserRoleEnum.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据代码获取枚举
     * @param code 代码字符串
     * @return 对应的枚举对象，如果未找到则返回 null
     */
    public static UserRoleEnum getByCode(String code) {
        for (UserRoleEnum type : UserRoleEnum.values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
