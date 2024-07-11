package com.wz.pilipili.vo.user;

import lombok.Data;

import java.util.Date;

/**
 * 用户偏好 对应视频的操作表 t_video_operation , 利用操作进行打分，统计得分计算value
 */
@Data
public class UserPreference {

    private Long id;

    private Long userId;

    private Long videoId;

    private Float value;//计算的值

    private Date createTime;
}
