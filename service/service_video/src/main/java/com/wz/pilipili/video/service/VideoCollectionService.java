package com.wz.pilipili.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.video.Video;
import com.wz.pilipili.entity.video.VideoCollection;
import com.wz.pilipili.entity.video.VideoCollectionGroup;

import java.util.Map;

/**
 * <p>
 * 视频收藏记录表 服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-17
 */
public interface VideoCollectionService extends IService<VideoCollection> {

    void collectVideo(Long userId, VideoCollection videoCollection);

    void updateVideoCollection(Long userId, VideoCollection videoCollection);

    void cancelCollectVideo(Long userId, Long videoId);

    Map<String,Object> getVideoCollections(Long userId, Long videoId);

    void addVideoCollectionGroup(Long userId, VideoCollectionGroup videoCollectionGroup);

    void deleteCollectionGroup(Long userId, Long groupId);

    PageResult<VideoCollection> pageGroupVideos(Integer no, Integer size, Integer groupId, Long userId);

    void updateCollectionGroup(Long userId, VideoCollectionGroup videoCollectionGroup);
}
