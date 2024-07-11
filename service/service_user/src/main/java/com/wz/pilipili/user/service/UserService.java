package com.wz.pilipili.user.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.user.User;
import com.wz.pilipili.entity.user.UserInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface UserService extends IService<User> {

    void register(User user);

    String login(User user) throws Exception;

    User getUserInfo(Long userId);

    void updateUser(User user) throws Exception;

    void updateUserInfo(UserInfo userInfo);

    PageResult<UserInfo> pageListUserInfos(JSONObject params);

    Map<String, Object> loginForDts(User user) throws Exception;

    void logout(Long userId);

    String refreshAccessToken(String refreshToken) throws Exception;

    List<UserInfo> batchGetUserInfoListByUserIds(Set<Long> userIds);

    UserInfo getUserInfoByUserId(Long userId);
}
