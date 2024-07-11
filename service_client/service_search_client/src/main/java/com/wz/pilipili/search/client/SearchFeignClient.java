package com.wz.pilipili.search.client;

import com.wz.pilipili.entity.user.UserInfo;
import com.wz.pilipili.entity.video.Video;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "service-search",url = "/search/inner")
public interface SearchFeignClient {


    @PostMapping("/addUserInfo")
    public void addUserInfo(@RequestBody UserInfo userInfo);

    @PostMapping("/addVideo")
    public void addVideo(@RequestBody Video video);

}
