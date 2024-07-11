package com.wz.pilipili.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.video.VideoLike;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 视频点赞记录表 服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-16
 */
public interface VideoLikeService extends IService<VideoLike> {

    void likeVideo(Long userId, Long videoId);

    void cancelLikeVideo(Long userId, Long videoId);

    Map<String, Object> getVideoLikes(Long userId, Long videoId);

    PageResult<VideoLike> pageListVideoLikes(Integer pageNo, Integer pageSize, Long userId, String startTime,String endTime);

    Integer getVideoLikesByVideoIds(Set<Long> videoIds);
}
