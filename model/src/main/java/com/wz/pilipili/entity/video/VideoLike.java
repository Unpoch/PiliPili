package com.wz.pilipili.entity.video;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_video_like")
public class VideoLike {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long videoId;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
