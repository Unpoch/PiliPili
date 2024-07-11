package com.wz.pilipili.entity.media;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
@TableName("t_media_process")
public class MediaProcess implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String md5;//文件md5，文件唯一标识

    private String fileName;

    private String bucket;

    private String objectName;

    /**
     * 状态 1:未处理，2：处理成功  3：处理失败 4：处理中
     */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    private Date finishTime;

    private String url;

    private int failCount;

    private String errormsg;






}
