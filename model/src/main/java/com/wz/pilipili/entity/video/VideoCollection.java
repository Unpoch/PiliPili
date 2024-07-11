package com.wz.pilipili.entity.video;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_video_collection")
public class VideoCollection {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long videoId;

    private Long userId;

    private Long groupId;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(exist = false)
    private Video videoInfo;
}
