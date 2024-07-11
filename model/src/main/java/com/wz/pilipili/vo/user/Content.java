package com.wz.pilipili.vo.user;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Date;

/**
 * 动态关联的内容详情类 可以是视频、专栏、直播，目前暂时不实现
 */
@Data
public class Content {

    private Long id;

    private JSONObject contentDetail;

    private Date createTime;
}
