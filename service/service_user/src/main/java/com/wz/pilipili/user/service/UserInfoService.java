package com.wz.pilipili.user.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.user.UserInfo;

import java.util.List;
import java.util.Set;

/**
 * 用户基本信息表 服务类
 *
 * @author unkonwnzz
 * @since 2024-05-26
 */
public interface UserInfoService extends IService<UserInfo> {

    UserInfo getUserInfoByUserId(Long userId);

    List<UserInfo> getUserInfoListByUserIds(Set<Long> userIds);

    IPage<UserInfo> pageListUserInfos(JSONObject params);
}
