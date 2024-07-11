package com.wz.pilipili.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.video.Video;
import com.wz.pilipili.entity.video.VideoCollectionGroup;
import com.wz.pilipili.exception.ConditionException;
import com.wz.pilipili.video.mapper.VideoCollectionGroupMapper;
import com.wz.pilipili.video.service.VideoCollectionGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wz.pilipili.video.service.VideoCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 收藏分组表 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-17
 */
@Service
public class VideoCollectionGroupServiceImpl extends ServiceImpl<VideoCollectionGroupMapper, VideoCollectionGroup> implements VideoCollectionGroupService {


    /**
     * 添加收藏夹（更改收藏夹）
     */
    @Override
    public void addVideoCollectionGroup(Long userId, VideoCollectionGroup videoCollectionGroup) {
        //1. 收藏夹名称和类型参数校验
        String groupName = videoCollectionGroup.getName();
        String type = videoCollectionGroup.getType();
        //根据userId和 groupName去查询是否存在同名的收藏夹
        int count = getGroupByUserIdAndGroupName(userId, groupName);
        if (type.equals("0") || count > 0) {
            throw new ConditionException("参数异常或收藏夹已存在！");
        }
        //2.插入记录
        videoCollectionGroup.setUserId(userId);
        baseMapper.insert(videoCollectionGroup);
    }

    /**
     * 删除收藏夹
     */
    @Override
    public void deleteCollectionGroup(Long userId, Long groupId) {
        baseMapper.delete(new LambdaQueryWrapper<VideoCollectionGroup>()
                .eq(VideoCollectionGroup::getId, groupId)
                .eq(VideoCollectionGroup::getUserId, userId));
    }


    /**
     * 更新收藏夹
     */
    @Override
    public void updateCollectionGroup(Long userId, VideoCollectionGroup videoCollectionGroup) {
        //1.收藏夹是否已经存在
        String groupName = videoCollectionGroup.getName();
        int count = this.getGroupByUserIdAndGroupName(userId, groupName);
        if (count > 0) {
            throw new ConditionException("收藏夹已存在！");
        }
        //2.更新
        videoCollectionGroup.setUserId(userId);//可以不需要
        baseMapper.updateById(videoCollectionGroup);
    }


    /*
    根据userId和groupName查询记录数
     */
    private int getGroupByUserIdAndGroupName(Long userId, String groupName) {
        return baseMapper.selectCount(new LambdaQueryWrapper<VideoCollectionGroup>()
                .eq(VideoCollectionGroup::getUserId, userId)
                .eq(VideoCollectionGroup::getName, groupName));
    }

}
