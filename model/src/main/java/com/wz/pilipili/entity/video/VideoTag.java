package com.wz.pilipili.entity.video;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_video_tag")
public class VideoTag {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long videoId;

    private Long tagId;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
