package com.wz.pilipili.entity.auth;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_auth_role_menu")
public class AuthRoleMenu {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long roleId;

    private Long menuId;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    //冗余字段
    @TableField(exist = false)
    private AuthMenu authMenu;//权限菜单实体类
}
