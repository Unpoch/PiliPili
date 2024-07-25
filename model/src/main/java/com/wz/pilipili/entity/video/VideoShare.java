package com.wz.pilipili.entity.video;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 视频分享表
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-07-25
 */
@Data
@TableName("t_video_share")
public class VideoShare {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long videoId;

    private Long userId;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


}
