package com.wz.pilipili.entity.auth;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_auth_element_operation")
public class AuthElementOperation {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String elementName;

    private String elementCode;

    private String operationType;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
