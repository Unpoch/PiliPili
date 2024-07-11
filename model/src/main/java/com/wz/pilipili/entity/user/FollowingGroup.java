package com.wz.pilipili.entity.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName("t_following_group")
public class FollowingGroup {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String name;

    private String type;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(exist = false)
    private List<UserInfo> followingUserInfoList;//关注分组中的所关注用户的信息列表

    @TableField(exist = false)
    private Integer count;

}
