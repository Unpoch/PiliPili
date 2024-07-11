package com.wz.pilipili.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wz.pilipili.entity.video.VideoCoin;

/**
 * <p>
 * 视频硬币表 Mapper 接口
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-17
 */
public interface VideoCoinMapper extends BaseMapper<VideoCoin> {

    Long getVideoCoinsByVideoId(Long videoId);
}
