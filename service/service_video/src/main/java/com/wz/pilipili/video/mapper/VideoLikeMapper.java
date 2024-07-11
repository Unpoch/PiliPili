package com.wz.pilipili.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wz.pilipili.entity.video.VideoLike;

import java.util.Set;

/**
 * <p>
 * 视频点赞记录表 Mapper 接口
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-16
 */
public interface VideoLikeMapper extends BaseMapper<VideoLike> {

    /**
     * 根据videoId集合统计 视频点赞总数
     */
    Integer getVideoLikesByVideoIds(Set<Long> videoIds);
}
