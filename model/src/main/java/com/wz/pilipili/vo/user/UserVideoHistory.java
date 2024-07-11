package com.wz.pilipili.vo.user;

import lombok.Data;

import java.util.Date;

/**
 * 用户视频观看历史记录
 */
@Data
public class UserVideoHistory {

    private Long userId;

    private Long videoId;

    //视频链接
    private String url;

    //封面
    private String thumbnail;

    //标题
    private String title;

    //视频时长
    private String duration;

    //发布人名称
    private String nick;

    //播放量
    private Integer viewCount;

    //弹幕量
    private Integer danmuCount;

    //记录时间
    private Date createTime;
}
