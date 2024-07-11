package com.wz.pilipili.media.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

/**
 * 视频mimeType格式配置类
 */
@Configuration
public class VideoFormatConfig {

    @Bean
    public Set<String> videoMimeTypes() {
        Set<String> videoMimeTypes = new HashSet<>();
        // videoMimeTypes.add("video/mp4");
        videoMimeTypes.add("video/x-msvideo");
        videoMimeTypes.add("video/quicktime");
        videoMimeTypes.add("video/x-matroska");
        videoMimeTypes.add("video/x-flv");
        videoMimeTypes.add("video/x-ms-wmv");
        videoMimeTypes.add("video/webm");
        videoMimeTypes.add("video/mpeg");
        return videoMimeTypes;
    }
}