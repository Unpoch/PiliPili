package com.wz.pilipili.constant;

/**
 * 角色权限常量类（废弃），改用枚举类
 */
public interface AuthRoleConstant {

    /**
     * Lv0 ~ Lv6 角色编码常量类
     */
    public static final String ROLE_LV0 = "Lv0";

    public static final String ROLE_LV1 = "Lv1";
    public static final String ROLE_LV2 = "Lv2";
    public static final String ROLE_LV3 = "Lv3";
    public static final String ROLE_LV4 = "Lv4";
    public static final String ROLE_LV5 = "Lv5";
    public static final String ROLE_LV6 = "Lv6";

    /**
     * Lv0~Lv6 每个等级需要获取的经验值
     */
    public static final Integer LV0_EXPERIENCE = 0;
    public static final Integer LV1_EXPERIENCE = 100;//这里实际上需要答题获得，但是本系统没设计答题业务
    public static final Integer LV2_EXPERIENCE = 200;
    public static final Integer LV3_EXPERIENCE = 1500;
    public static final Integer LV4_EXPERIENCE = 4500;
    public static final Integer LV5_EXPERIENCE = 10800;
    public static final Integer LV6_EXPERIENCE = 28800;

}
