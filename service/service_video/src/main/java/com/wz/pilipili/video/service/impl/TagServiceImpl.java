package com.wz.pilipili.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wz.pilipili.entity.video.Tag;
import com.wz.pilipili.video.mapper.TagMapper;
import com.wz.pilipili.video.service.TagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 标签表 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-15
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    /**
     * 添加标签，并将标签返回
     */
    @Override
    public Long addTag(Tag tag) {
        baseMapper.insert(tag);
        return tag.getId();
    }

    /**
     * 根据标签id集合获取标签列表
     */
    @Override
    public List<Tag> getTagListByTagIds(Set<Long> tagIds) {
        return baseMapper.selectList(new LambdaQueryWrapper<Tag>()
                .in(Tag::getId, tagIds));
    }

}
