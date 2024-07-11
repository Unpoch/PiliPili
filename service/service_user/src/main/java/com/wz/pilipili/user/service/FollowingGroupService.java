package com.wz.pilipili.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.user.FollowingGroup;

import java.util.List;

/**
 * 用户关注分组表 服务类
 *
 * @author unkonwnzz
 * @since 2024-05-28
 */
public interface FollowingGroupService extends IService<FollowingGroup> {


    public FollowingGroup getByType(String type);


    List<FollowingGroup> getFollowingGroupListByUserId(Long userId);

    void addFollowingGroup(FollowingGroup followingGroup);

    void removeFollowingGroup(Long userId, Long groupId);
}
