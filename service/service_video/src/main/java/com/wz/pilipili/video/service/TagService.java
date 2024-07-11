package com.wz.pilipili.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.video.Tag;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 标签表 服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-15
 */
public interface TagService extends IService<Tag> {

    Long addTag(Tag tag);

    List<Tag> getTagListByTagIds(Set<Long> tagIds);

}
