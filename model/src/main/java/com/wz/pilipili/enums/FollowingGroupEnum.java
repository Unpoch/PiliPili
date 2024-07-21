package com.wz.pilipili.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 关注分组枚举类
 */
@Getter
@AllArgsConstructor
public enum FollowingGroupEnum {

    SPECIAL("0", "特别关注"),       // 特别关注
    QUIETLY("1", "悄悄关注"),      // 悄悄关注
    DEFAULT("2", "默认关注分组"),  // 默认关注分组
    USER("3", "用户自定义分组");   // 用户自定义分组（一般用不到）

    private final String type;
    private final String name;

    /**
     * 返回需要初始化的常量类
     */
    public static List<FollowingGroupEnum> getInitFollowingGroupEnums() {
        List<FollowingGroupEnum> specificValues = new ArrayList<>();
        for (FollowingGroupEnum followingGroupEnum : FollowingGroupEnum.values()) {
            if (followingGroupEnum.type.equals("0")
                    || followingGroupEnum.type.equals("1")
                    || followingGroupEnum.type.equals("2")) {
                specificValues.add(followingGroupEnum);
            }
        }
        return specificValues;
    }
}
