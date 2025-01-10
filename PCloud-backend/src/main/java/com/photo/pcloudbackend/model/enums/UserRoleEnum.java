package com.photo.pcloudbackend.model.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * 用户角色
 */
@Getter
public enum UserRoleEnum {
    USER("用户", "user"),
    ADMIN("管理员", "admin");
    private final String text;
    private final String value;
    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value获取枚举
     * @param value
     * @return
     */
    public static UserRoleEnum getEnumByValue(String value) {
        if(ObjectUtil.isEmpty(value)) {
            return null;
        }
        for(UserRoleEnum roleEnum : UserRoleEnum.values()) {
            if(roleEnum.getValue().equals(value)) {
                return roleEnum;
            }
        }
        return null;
    }
}
