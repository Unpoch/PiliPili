package com.wz.pilipili.entity.video;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_video_coin")
public class VideoCoin {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long videoId;

    private Long userId;

    private Integer amount;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
