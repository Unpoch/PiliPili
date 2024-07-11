package com.wz.pilipili.user.client;

import com.wz.pilipili.entity.user.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Set;

@FeignClient(value = "service-user", url = "/user/userinfo")
public interface UserInfoFeignClient {

    @GetMapping("/inner/batchGetUserInfo")
    public List<UserInfo> batchGetUserInfoListByUserIds(Set<Long> userIds);

    @GetMapping("/inner/getInfoById")
    public UserInfo getInfoById(Long userId);

}
