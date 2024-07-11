package com.wz.pilipili.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.video.Tag;
import com.wz.pilipili.entity.video.VideoTag;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 视频标签关联表 服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-15
 */
public interface VideoTagService extends IService<VideoTag> {

    void batchInsertVideoTags(List<VideoTag> videoTagList);

    void deleteVideoTags(List<Long> tagIdList, Long videoId);

    List<VideoTag> getVideoTagsByVideoId(Long videoId);

    Long addTag(Tag tag);

    List<Tag> getTagListByVideoId(Long videoId);

    List<VideoTag> getVideoTagListByVideoIds(Set<Long> videoIds);
}
