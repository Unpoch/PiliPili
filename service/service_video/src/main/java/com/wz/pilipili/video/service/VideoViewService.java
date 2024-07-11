package com.wz.pilipili.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.video.VideoView;
import com.wz.pilipili.vo.video.VideoViewCount;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 视频观看记录表 服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-17
 */
public interface VideoViewService extends IService<VideoView> {

    void addVideoView(VideoView videoView, HttpServletRequest request);

    Integer getViewCounts(Long videoId);

    List<VideoViewCount> getVideoViewCountByVideoIds(Set<Long> videoIds);

    PageResult<VideoView> pageListVideoViews(Integer pageNo, Integer pageSize, Long userId);

}
