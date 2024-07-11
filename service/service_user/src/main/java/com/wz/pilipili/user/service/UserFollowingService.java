package com.wz.pilipili.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.user.FollowingGroup;
import com.wz.pilipili.entity.user.UserFollowing;
import com.wz.pilipili.entity.user.UserInfo;

import java.util.List;

/**
 * 用户关注表 服务类
 *
 * @author unkonwnzz
 * @since 2024-05-28
 */
public interface UserFollowingService extends IService<UserFollowing> {

    void follow(UserFollowing userFollowing);

    List<FollowingGroup> getFollowingList(Long userId);

    List<UserFollowing> getFanList(Long userId);

    List<UserInfo> checkFollowingStatus(List<UserInfo> userInfoList, Long userId);

    void cancelFollow(Long userId, Long followingId);

    void updateUserFollowing(UserFollowing userFollowing);

    void addFollowingGroup(FollowingGroup followingGroup);

    List<FollowingGroup> getAllFollowingGroups(Long userId);

    void removeFollowingGroup(Long userId, Long groupId);

    PageResult<UserFollowing> pageListUserFollowings(Integer pageNo, Integer pageSize, Long userId, Long groupId);

    PageResult<UserFollowing> pageListUserFans(Integer pageNo, Integer pageSize, Long userId);

    Integer getFollowingCountByUserId(Long userId);

    Integer getFanCountByUserId(Long userId);
}
