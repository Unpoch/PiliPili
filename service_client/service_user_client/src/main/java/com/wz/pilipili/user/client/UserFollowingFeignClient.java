package com.wz.pilipili.user.client;

import com.wz.pilipili.entity.user.UserFollowing;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(value = "service-user") //指定被调用方在注册中心的应用名称
public interface UserFollowingFeignClient {

    /**
     * 获取用户粉丝列表
     */
    @GetMapping("/user/following/inner/user-fans")
    public List<UserFollowing> getUserFans(Long userId);
}
