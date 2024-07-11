package com.wz.pilipili.entity.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_user_following")
public class UserFollowing {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long followingId;

    private Long groupId;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(exist = false)
    private UserInfo userInfo;//冗余，将关注者followingId 和其用户信息做关联


}
