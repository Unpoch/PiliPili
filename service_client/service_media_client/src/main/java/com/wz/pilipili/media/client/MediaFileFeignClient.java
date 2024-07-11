package com.wz.pilipili.media.client;

import com.wz.pilipili.vo.media.FileInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 媒资服务Feign客户端
 */
@FeignClient(value = "service-media", url = "/media/file/inner")
public interface MediaFileFeignClient {

    @GetMapping("/getFileInfo")
    public FileInfo getFileInfoByFileId(Integer fileId) throws Exception;

}
