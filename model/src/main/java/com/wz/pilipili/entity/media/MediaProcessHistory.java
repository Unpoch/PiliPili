package com.wz.pilipili.entity.media;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("t_media_process_history")
public class MediaProcessHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String md5;

    private String fileName;

    private String bucket;

    private String objectName;

    /**
     * 状态 1:未处理，视频处理完成更新为2
     */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    private Date finishTime;

    private String url;

    private String errormsg;

    private int failCount;


}
