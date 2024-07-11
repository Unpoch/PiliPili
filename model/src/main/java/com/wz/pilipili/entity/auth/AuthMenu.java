package com.wz.pilipili.entity.auth;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_auth_menu")
public class AuthMenu {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String code;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
