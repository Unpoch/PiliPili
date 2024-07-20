package com.wz.pilipili.enums;

import lombok.Getter;

/**
 * 角色等级枚举类
 */
@Getter
public enum RoleLevel {
    LV0("Lv0", 0),
    LV1("Lv1", 100),
    LV2("Lv2", 200),
    LV3("Lv3", 1500),
    LV4("Lv4", 4500),
    LV5("Lv5", 10800),
    LV6("Lv6", 28800);

    private final String roleCode;
    private final Integer experience;

    RoleLevel(String role, Integer experience) {
        this.roleCode = role;
        this.experience = experience;
    }

    /**
     * 根据当前角色编码获取对应等级需要的经验值
     */
    public static Integer getExperienceByRoleCode(String roleCode) {
        for (RoleLevel level : values()) {
            if (level.getRoleCode().equalsIgnoreCase(roleCode)) {
                return level.getExperience();
            }
        }
        throw new IllegalArgumentException("无效的角色编码: " + roleCode);
    }

    /**
     * 获取当前角色编码的下一个角色编码
     */
    public static String getNextRoleCode(String curRoleCode) {
        if (curRoleCode.equals(RoleLevel.LV6.roleCode)) {
            throw new RuntimeException("Lv6已经是最高等级！");
        }
        String res = "";
        RoleLevel[] levels = RoleLevel.values();
        for (int i = 0; i < levels.length - 1; i++) {
            if (levels[i].getRoleCode().equalsIgnoreCase(curRoleCode)) {
                res = levels[i + 1].roleCode;
                break;
            }
        }
        return res;
    }
}
