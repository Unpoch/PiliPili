package com.wz.pilipili.entity.video;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_tag")
public class Tag {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
