package com.wz.pilipili.user.service;

import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.user.UserFollowing;
import com.wz.pilipili.entity.user.UserInfo;
import com.wz.pilipili.entity.user.UserMoments;
import com.wz.pilipili.entity.video.Video;

public interface UserViewedService {
    UserInfo getViewedUserInfo(Long userId);

    PageResult<Video> getViewedUserVideos(Integer pageNo, Integer pageSize, Long userId);

    PageResult<UserMoments> getViewedUserMoments(Integer pageNo, Integer pageSize, Long userId);

    PageResult<UserFollowing> getViewedUserFollowings(Integer pageNo, Integer pageSize, Long userId);

    PageResult<UserFollowing> getViewedUserFans(Integer pageNo, Integer pageSize, Long userId);
}
