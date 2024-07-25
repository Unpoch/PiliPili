package com.wz.pilipili.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wz.pilipili.entity.video.VideoShare;
import com.wz.pilipili.video.mapper.VideoShareMapper;
import com.wz.pilipili.video.service.VideoShareService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 视频分享表 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-07-25
 */
@Service
public class VideoShareServiceImpl extends ServiceImpl<VideoShareMapper, VideoShare> implements VideoShareService {


    /**
     * 添加视频分享链接
     */
    @Override
    public void addVideoShare(VideoShare videoShare) {
        baseMapper.insert(videoShare);
    }

    /**
     * 根据videoId获取视频分享数
     */
    @Override
    public Integer getVideoShareCount(Long videoId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<VideoShare>()
                .eq(VideoShare::getVideoId, videoId));
    }

    /**
     * 根据userId和videoId查询视频分享记录
     */
    @Override
    public VideoShare getVideoShareByVideoIdAndUserId(Long userId, Long videoId) {
        return baseMapper.selectOne(new LambdaQueryWrapper<VideoShare>()
                .eq(VideoShare::getUserId, userId)
                .eq(VideoShare::getVideoId, videoId));
    }
}
