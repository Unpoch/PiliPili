package com.wz.pilipili.entity.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_refresh_token")
public class RefreshTokenDetail {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String refreshToken;

    private Long userId;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
