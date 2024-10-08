package com.wz.pilipili.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wz.pilipili.constant.UserConstant;
import com.wz.pilipili.entity.user.FollowingGroup;
import com.wz.pilipili.enums.FollowingGroupEnum;
import com.wz.pilipili.user.mapper.FollowingGroupMapper;
import com.wz.pilipili.user.service.FollowingGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户关注分组表 服务实现类
 *
 * @author unkonwnzz
 * @since 2024-05-28
 */
@Service
public class FollowingGroupServiceImpl extends ServiceImpl<FollowingGroupMapper, FollowingGroup> implements FollowingGroupService {

    /**
     * 根据type获取FollowingGroup
     */
    @Override
    public FollowingGroup getByType(String type) {
        return this.getOne(new LambdaQueryWrapper<FollowingGroup>().eq(FollowingGroup::getType, type));
    }

    /**
     * 根据用户id 获取其所有分组
     */
    @Override
    public List<FollowingGroup> getFollowingGroupListByUserId(Long userId) {
        return this.list(new LambdaQueryWrapper<FollowingGroup>()
                .eq(FollowingGroup::getUserId, userId));
    }

    /**
     * 添加用户关注分组
     */
    @Override
    public void addFollowingGroup(FollowingGroup followingGroup) {
        baseMapper.insert(followingGroup);
    }

    /**
     * 删除用户关注分组
     */
    @Override
    public void removeFollowingGroup(Long userId, Long groupId) {
        baseMapper.delete(new LambdaQueryWrapper<FollowingGroup>()
                .eq(FollowingGroup::getUserId, userId)
                .eq(FollowingGroup::getId, groupId));
    }

    /**
     * 初始化用户关注分组：特别关注，悄悄关注，默认分组
     */
    @Override
    public void initUserFollowingGroup(Long userId) {
        //这里只会返回 特别关注，悄悄关注，默认分组这三种
        List<FollowingGroupEnum> initFollowingGroupEnums = FollowingGroupEnum.getInitFollowingGroupEnums();
        for (FollowingGroupEnum followingGroupEnum : initFollowingGroupEnums) {
            FollowingGroup group = new FollowingGroup();
            group.setUserId(userId);
            group.setName(followingGroupEnum.getName());
            group.setType(followingGroupEnum.getType());
            baseMapper.insert(group);
        }
    }
}
