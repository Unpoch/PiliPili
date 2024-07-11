package com.wz.pilipili.entity.video;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

@Data
@TableName("t_video")
@Document(indexName = "videos") //es 索引
public class Video {

    @Id     //es主键
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Field(type = FieldType.Long)
    private Long userId;//用户id

    private String url; //视频链接

    private String thumbnail;//封面

    @Field(type = FieldType.Text) //使用Text 会进行分词
    private String title; //标题

    private String type;// 0自制 1转载

    private String duration;//时长

    private String area;//分区

    @Field(type = FieldType.Text)
    private String description;//简介

    @Field(type = FieldType.Date)
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Field(type = FieldType.Date)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Field(type = FieldType.Integer)
    @TableField(exist = false)
    private Integer viewCount;

    @Field(type = FieldType.Integer)
    @TableField(exist = false)
    private Integer danmuCount;

    @TableField(exist = false)
    private List<VideoTag> tagList;//标签列表
}
