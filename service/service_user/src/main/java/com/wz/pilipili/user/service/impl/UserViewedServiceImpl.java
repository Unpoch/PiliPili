package com.wz.pilipili.user.service.impl;

import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.user.UserFollowing;
import com.wz.pilipili.entity.user.UserInfo;
import com.wz.pilipili.entity.user.UserMoments;
import com.wz.pilipili.entity.video.Video;
import com.wz.pilipili.user.service.UserFollowingService;
import com.wz.pilipili.user.service.UserInfoService;
import com.wz.pilipili.user.service.UserMomentsService;
import com.wz.pilipili.user.service.UserViewedService;
import com.wz.pilipili.video.client.VideoFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 获取正在查看用户信息 服务类
 */
@Service
public class UserViewedServiceImpl implements UserViewedService {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserMomentsService userMomentsService;

    @Autowired
    private UserFollowingService userFollowingService;

    @Autowired
    private VideoFeignClient videoFeignClient;


    /**
     * 获取用户信息
     */
    @Override
    public UserInfo getViewedUserInfo(Long userId) {
        UserInfo viewedUserInfo = userInfoService.getUserInfoByUserId(userId);
        //获取关注数
        Integer followingCount = userFollowingService.getFollowingCountByUserId(userId);
        //获取点赞数
        Integer fanCount = userFollowingService.getFanCountByUserId(userId);
        //获取点赞数
        Integer likeCount = videoFeignClient.getUserVideoLikes(userId);
        //设置UserInfo的属性
        viewedUserInfo.setFollowingCount(followingCount);
        viewedUserInfo.setFanCount(fanCount);
        viewedUserInfo.setLikeCount(likeCount);
        return viewedUserInfo;
    }

    /**
     * 分页获取用户投稿视频
     */
    @Override
    public PageResult<Video> getViewedUserVideos(Integer pageNo, Integer pageSize, Long userId) {
        return videoFeignClient.pageListUserVideos(pageNo, pageSize, userId);
    }

    /**
     * 分页查询用户动态
     */
    @Override
    public PageResult<UserMoments> getViewedUserMoments(Integer pageNo, Integer pageSize, Long userId) {
        return userMomentsService.pageListUserMoments(pageNo, pageSize, userId);
    }

    /**
     * 分页查询用户关注
     */
    @Override
    public PageResult<UserFollowing> getViewedUserFollowings(Integer pageNo, Integer pageSize, Long userId) {
        return userFollowingService.pageListUserFollowings(pageNo, pageSize, userId, null);
    }

    /**
     * 分页查询用户粉丝
     */
    @Override
    public PageResult<UserFollowing> getViewedUserFans(Integer pageNo, Integer pageSize, Long userId) {
        return userFollowingService.pageListUserFans(pageNo, pageSize, userId);
    }


}
