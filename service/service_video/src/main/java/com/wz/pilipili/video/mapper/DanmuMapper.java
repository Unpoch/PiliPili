package com.wz.pilipili.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wz.pilipili.entity.video.Danmu;
import com.wz.pilipili.vo.video.VideoDanmuCount;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 弹幕记录表 Mapper 接口
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-22
 */
public interface DanmuMapper extends BaseMapper<Danmu> {

    List<VideoDanmuCount> getVideoDanmuCountByVideoIds(Set<Long> videoIds);
}
