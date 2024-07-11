package com.wz.pilipili.entity.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@Document(indexName = "user-infos")
@TableName("t_user_info")
public class UserInfo {

    @Id
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    @Field(type = FieldType.Text)
    private String nick;

    private String avatar;

    private String sign;

    private String gender;

    private String birth;

    @Field(type = FieldType.Date)
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Field(type = FieldType.Date)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    //冗余字段
    @TableField(exist = false)
    private Boolean followed;//true表示关注，false表示未关注

    //粉丝数
    @Field(type = FieldType.Integer)
    @TableField(exist = false)
    private Integer fanCount;

    //关注数
    @TableField(exist = false)
    private Integer followingCount;

    //点赞数
    @TableField(exist = false)
    private Integer likeCount;

    //TODO:视频投稿数
}
