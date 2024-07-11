package com.wz.pilipili.entity.auth;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_auth_role_element_operation")
public class AuthRoleElementOperation {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long roleId;

    private Long elementOperationId;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    //冗余字段
    @TableField(exist = false)
    private AuthElementOperation authElementOperation;//页面元素操作实体类
}
