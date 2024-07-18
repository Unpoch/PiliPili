package com.wz.pilipili.constant;

/**
 * Redis key常量类
 */
public interface RedisConstant {

    /**
     * 粉丝订阅用户动态的key，对应的是粉丝的 订阅的动态列表
     */
    public static final String SUBSCRIBE_MOMENTS = "subscribe-";


    /**
     * 分块上传文件的key
     */

    //上传的大小
    public static final String UPLOADED_SIZE_KEY = "uploaded-size-key:";

    //上传的第几个分块
    public static final String UPLOADED_NO_KEY = "uploaded-no-key:";


    /**
     * 保存弹幕的key
     */
    public static final String VIDEO_DANMU = "dm-video:";

    /**
     * 用户登录时间key
     */
    public static final String LOGIN_TIME = "user-login-time:";

    /**
     * 用户每日首次观看视频key，值为"1"，有值则说明当日已看过
     */
    public static final String DAILY_WATCHED_VIDEO = "user-watched-video:";
}
