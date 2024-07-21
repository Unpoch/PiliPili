package com.wz.pilipili.video.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("service-video")
public interface VideoCollectionFeignClient {

    /**
     * 初始化用户默认收藏夹
     */
    @PostMapping("/inner/addDefaultVideoCollectionGroup")
    public void addDefaultVideoCollectionGroup(@RequestParam Long userId);
}
