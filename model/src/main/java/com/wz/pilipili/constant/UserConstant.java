package com.wz.pilipili.constant;

/**
 * 用户常量
 */
public interface UserConstant {

    /**
     * 性别
     */
    public static final String GENDER_MALE = "0";

    public static final String GENDER_FEMALE = "1";

    public static final String GENDER_UNKNOW = "2";

    /**
     * 默认生日
     */
    public static final String DEFAULT_BIRTH = "1999-10-01";

    /**
     * 默认昵称
     */
    public static final String DEFAULT_NICK = "萌新";

    /**
     * 默认关注分组
     */
    public static final String USER_FOLLOWING_GROUP_TYPE_SPECIAL = "0";//特别关注

    public static final String USER_FOLLOWING_GROUP_TYPE_QUIETLY = "1";//悄悄关注

    public static final String USER_FOLLOWING_GROUP_TYPE_DEFAULT = "2";//默认关注分组

    public static final String USER_FOLLOWING_GROUP_TYPE_USER = "3";//用户自定义分组

    public static final String USER_FOLLOWING_GROUP_ALL_NAME = "全部关注";

    /**
     * 视频收藏分组类型
     */
    public static final String VIDEO_COLLECTION_GROUP_TYPE_DEFAULT = "0";//默认收藏分组
    public static final String VIDEO_COLLECTION_GROUP_TYPE_USER = "1";//用户自定义分组

    /**
     * 经验值
     */
    public static final Integer ONE_EXPERIENCE = 1;
    public static final Integer FIVE_EXPERIENCE = 5;
    public static final Integer TEN_EXPERIENCE = 10;
    public static final Integer THIRTY_EXPERIENCE = 30;
    public static final Integer FIFTY_EXPERIENCE = 50;

    public static final Integer DAILY_MAX_EXPERIENCE = 50;

    /**
     * 硬币数
     */
    public static final Integer ONE_COIN = 1;
    public static final Integer TWO_COIN = 2;
}
