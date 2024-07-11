package com.wz.pilipili.entity.auth;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_user_role")
public class UserRole {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long roleId;

    @TableField(exist = false)
    private String roleName;//t_auth_role中的name字段

    @TableField(exist = false)
    private String roleCode;//t_auth_role中的code字段

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
