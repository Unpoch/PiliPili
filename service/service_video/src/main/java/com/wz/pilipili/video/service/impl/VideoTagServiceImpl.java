package com.wz.pilipili.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wz.pilipili.entity.video.Tag;
import com.wz.pilipili.entity.video.VideoTag;
import com.wz.pilipili.video.mapper.VideoTagMapper;
import com.wz.pilipili.video.service.TagService;
import com.wz.pilipili.video.service.VideoTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 视频标签关联表 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-15
 */
@Service
public class VideoTagServiceImpl extends ServiceImpl<VideoTagMapper, VideoTag> implements VideoTagService {

    @Autowired
    private TagService tagService;

    /**
     * 批量添加VideoTag
     */
    @Override
    public void batchInsertVideoTags(List<VideoTag> videoTagList) {
        this.saveBatch(videoTagList);
    }

    /**
     * 删除视频相关标签
     */
    @Override
    public void deleteVideoTags(List<Long> tagIdList, Long videoId) {
        baseMapper.delete(new LambdaQueryWrapper<VideoTag>()
                .eq(VideoTag::getVideoId, videoId)
                .in(VideoTag::getTagId, tagIdList));
    }

    /**
     * 根据视频id获取视频标签
     */
    @Override
    public List<VideoTag> getVideoTagsByVideoId(Long videoId) {
        return baseMapper.selectList(new LambdaQueryWrapper<VideoTag>()
                .eq(VideoTag::getVideoId, videoId));
    }

    /**
     * 添加标签
     */
    @Override
    public Long addTag(Tag tag) {
        return tagService.addTag(tag);
    }

    /**
     * 根据videoId查询 视频标签
     */
    @Override
    public List<Tag> getTagListByVideoId(Long videoId) {
        List<VideoTag> videoTagList = this.getVideoTagsByVideoId(videoId);
        Set<Long> tagIds = videoTagList.stream().map(VideoTag::getTagId).collect(Collectors.toSet());
        return tagService.getTagListByTagIds(tagIds);
    }

    /**
     * 根据videoId集合 查询 List<VideoTag>
     */
    @Override
    public List<VideoTag> getVideoTagListByVideoIds(Set<Long> videoIds) {
        return baseMapper.selectList(new LambdaQueryWrapper<VideoTag>()
                .in(VideoTag::getVideoId, videoIds));
    }
}
