package com.wz.pilipili.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wz.pilipili.entity.video.VideoView;
import com.wz.pilipili.vo.video.VideoViewCount;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 视频观看记录表 Mapper 接口
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-17
 */
public interface VideoViewMapper extends BaseMapper<VideoView> {

    /**
     * 查询观看记录
     */
    VideoView getVideoView(Map<String, Object> params);

    List<VideoViewCount> getVideoViewCountByVideoIds(Set<Long> videoIds);
}
