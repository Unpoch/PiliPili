package com.wz.pilipili.video.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "service-video",url = "/video/like")
public interface VideoLikeFeignClient {

    // @GetMapping("/inner/getUserVideoLikes")
    // public Integer getUserVideoLikes(@RequestParam Long userId);
}
