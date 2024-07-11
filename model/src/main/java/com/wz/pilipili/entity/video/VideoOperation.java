package com.wz.pilipili.entity.video;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_video_operation")
public class VideoOperation {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long videoId;

    private String operationType;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
