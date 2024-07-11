package com.wz.pilipili.entity.video;

import com.baomidou.mybatisplus.annotation.*;
import com.wz.pilipili.entity.user.UserInfo;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName("t_video_comment")
public class VideoComment {

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    private Long videoId;

    private Long userId;//评论创建者id

    private String comment;

    private Long replyUserId;//该评论回复的用户的id（乙回复甲，那么就是甲的userId）

    private Long rootId;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(exist = false)
    private List<VideoComment> childList;//二级评论

    @TableField(exist = false)
    private UserInfo userInfo;//发表该评论的用户信息

    @TableField(exist = false)
    private UserInfo replyUserInfo;//该评论回复的用户的信息

}
