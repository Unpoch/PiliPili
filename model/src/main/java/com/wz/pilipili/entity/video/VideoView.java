package com.wz.pilipili.entity.video;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_video_view")
public class VideoView {

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    private Long videoId;

    private Long userId;

    private String clientId;//操作系统 + 浏览器

    private String ip;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
