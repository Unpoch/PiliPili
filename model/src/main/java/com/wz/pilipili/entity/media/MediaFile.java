package com.wz.pilipili.entity.media;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("t_media_file")
public class MediaFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String fileName;

    private String bucket;

    private String objectName;

    private String md5;

    private String url;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
