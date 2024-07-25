package com.wz.pilipili.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.video.VideoShare;

/**
 * <p>
 * 视频分享表 服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-07-25
 */
public interface VideoShareService extends IService<VideoShare> {

    void addVideoShare(VideoShare videoShare);

    Integer getVideoShareCount(Long videoId);

    VideoShare getVideoShareByVideoIdAndUserId(Long userId, Long videoId);
}
