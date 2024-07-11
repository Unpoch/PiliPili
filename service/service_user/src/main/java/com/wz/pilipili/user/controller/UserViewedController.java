package com.wz.pilipili.user.controller;

import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.user.UserFollowing;
import com.wz.pilipili.entity.user.UserInfo;
import com.wz.pilipili.entity.user.UserMoments;
import com.wz.pilipili.entity.video.Video;
import com.wz.pilipili.result.R;
import com.wz.pilipili.user.service.UserViewedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户中心
 */
@RestController
@RequestMapping("/user/viewed")
public class UserViewedController {

    @Autowired
    private UserViewedService userViewedService;

    /**
     * TODO：查询视频分区（暂时不做）
     */

    /**
     * 查询用户信息：昵称，签名，头像，点赞数，关注数，粉丝数
     */
    @GetMapping("/getViewedUserInfo")
    public R<UserInfo> getViewedUserInfo(@RequestParam Long userId) {
        UserInfo userInfo = userViewedService.getViewedUserInfo(userId);
        return new R<>(userInfo);
    }

    /**
     * 分页查询用户视频
     */
    @GetMapping("/getViewedUserVideos")
    public R<PageResult<Video>> getViewedUserVideos(@RequestParam Integer pageNo,
                                                    @RequestParam Integer pageSize,
                                                    @RequestParam Long userId) {
        PageResult<Video> pageResult = userViewedService.getViewedUserVideos(pageNo, pageSize, userId);
        return new R<>(pageResult);
    }

    /**
     * 分页查询用户动态
     */
    @GetMapping("/getViewedUserMoments")
    public R<PageResult<UserMoments>> getViewedUserMoments(@RequestParam Integer pageNo,
                                                           @RequestParam Integer pageSize,
                                                           @RequestParam Long userId) {
        PageResult<UserMoments> pageResult = userViewedService.getViewedUserMoments(pageNo, pageSize, userId);
        return new R<>(pageResult);
    }

    /**
     * 分页查询用户关注
     */
    @GetMapping("/getViewedUserFollowings")
    public R<PageResult<UserFollowing>> getViewedUserFollowings(@RequestParam Integer pageNo,
                                                                @RequestParam Integer pageSize,
                                                                @RequestParam Long userId) {
        PageResult<UserFollowing> pageResult = userViewedService.getViewedUserFollowings(pageNo, pageSize, userId);
        return new R<>(pageResult);
    }

    /**
     * 分页查询用户粉丝
     */
    @GetMapping("/getViewedUserFans")
    public R<PageResult<UserFollowing>> getViewedUserFans(@RequestParam Integer pageNo,
                                                          @RequestParam Integer pageSize,
                                                          @RequestParam Long userId) {
        PageResult<UserFollowing> pageResult = userViewedService.getViewedUserFans(pageNo, pageSize, userId);
        return new R<>(pageResult);
    }


    /**
     * TODO：分页查询用户空间收藏（暂时不做）
     */

}
