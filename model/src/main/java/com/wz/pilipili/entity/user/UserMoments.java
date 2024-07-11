package com.wz.pilipili.entity.user;

import com.baomidou.mybatisplus.annotation.*;
import com.wz.pilipili.vo.user.Content;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_user_moments")
public class UserMoments {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String type;

    private Long contentId;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    //辅助字段
    @TableField(exist = false)
    private Content content;//动态关联的内容

    //辅助字段
    @TableField(exist = false)
    private UserInfo userInfo;
}
