package com.wz.pilipili.entity.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_user")
public class User {

    @TableId(value = "id", type = IdType.AUTO) //指定主键id 和自增策略
    private Long id;

    private String phone;

    private String email;

    private String password;

    private String salt;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    //冗余字段
    @TableField(exist = false)
    private UserInfo userInfo;
}
