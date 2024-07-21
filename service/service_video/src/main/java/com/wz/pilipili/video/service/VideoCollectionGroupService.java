package com.wz.pilipili.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.video.Video;
import com.wz.pilipili.entity.video.VideoCollectionGroup;

/**
 * <p>
 * 收藏分组表 服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-17
 */
public interface VideoCollectionGroupService extends IService<VideoCollectionGroup> {

    void addVideoCollectionGroup(Long userId, VideoCollectionGroup videoCollectionGroup);

    void deleteCollectionGroup(Long userId, Long groupId);

    void updateCollectionGroup(Long userId, VideoCollectionGroup videoCollectionGroup);

    void addDefaultVideoCollectionGroup(Long userId);
}
