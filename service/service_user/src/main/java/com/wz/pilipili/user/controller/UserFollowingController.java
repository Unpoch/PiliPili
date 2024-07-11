package com.wz.pilipili.user.controller;


import com.wz.pilipili.context.UserContext;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.user.FollowingGroup;
import com.wz.pilipili.entity.user.UserFollowing;
import com.wz.pilipili.result.R;
import com.wz.pilipili.user.service.FollowingGroupService;
import com.wz.pilipili.user.service.UserFollowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户关注
 *
 * @author unkonwnzz
 * @since 2024-05-28
 */
@RestController
@RequestMapping("/user/following")
public class UserFollowingController {

    @Autowired
    private UserFollowingService userFollowingService;


    /**
     * 关注用户（添加用户关注）
     */
    @PostMapping("/follow")
    public R<String> follow(@RequestBody UserFollowing userFollowing) {
        Long userId = UserContext.getCurUserId();
        userFollowing.setUserId(userId);
        userFollowingService.follow(userFollowing);
        return R.success();
    }


    /**
     * 关注列表
     */
    @GetMapping("/user-followings")
    public R<List<FollowingGroup>> getFollowingList() {
        Long userId = UserContext.getCurUserId();
        //FollowingGroup类里有一个字段为:followingUserInfoList
        List<FollowingGroup> userFollowingList = userFollowingService.getFollowingList(userId);
        return new R<>(userFollowingList);
    }

    /**
     * 删除用户关注（取关）
     */
    @PostMapping("/cancelFollow")
    public R<String> cancelFollow(@RequestParam Long followingId) {
        Long userId = UserContext.getCurUserId();
        userFollowingService.cancelFollow(userId, followingId);
        return R.success();
    }

    /**
     * 更新用户关注（换分组）
     */
    @PostMapping("/updateUserFollowing")
    public R<String> updateUserFollowing(@RequestBody UserFollowing userFollowing) {
        Long userId = UserContext.getCurUserId();
        userFollowing.setUserId(userId);
        userFollowingService.updateUserFollowing(userFollowing);
        return R.success();
    }

    /**
     * 粉丝列表（不分页）
     */
    @GetMapping("/user-fans")
    public R<List<UserFollowing>> getFanList() {
        Long userId = UserContext.getCurUserId();
        List<UserFollowing> userFollowingList = userFollowingService.getFanList(userId);
        return new R<>(userFollowingList);
    }

    /**
     * 添加用户关注分组
     */
    @PostMapping("/addFollowingGroup")
    public R<String> addFollowingGroup(@RequestBody FollowingGroup followingGroup) {
        Long userId = UserContext.getCurUserId();
        followingGroup.setUserId(userId);
        userFollowingService.addFollowingGroup(followingGroup);
        return R.success();
    }

    /**
     * 获取用户所有关注分组（仅获取关注分组信息，不包含分组下关注人信息）
     */
    @GetMapping("/getAllFollowingGroups")
    public R<List<FollowingGroup>> getAllFollowingGroups() {
        Long userId = UserContext.getCurUserId();
        List<FollowingGroup> result = userFollowingService.getAllFollowingGroups(userId);
        return new R<>(result);
    }


    /**
     * 删除用户关注分组
     */
    @PostMapping("/removeFollowingGroup")
    public R<String> removeFollowingGroup(@RequestParam Long groupId) {
        Long userId = UserContext.getCurUserId();
        userFollowingService.removeFollowingGroup(userId, groupId);
        return R.success();
    }

    /**
     * 分页查询用户关注
     */
    @GetMapping("/pageListUserFollowings")
    public R<PageResult<UserFollowing>> pageListUserFollowings(@RequestParam Integer pageNo,
                                                               @RequestParam Integer pageSize,
                                                               Long groupId) {
        Long userId = UserContext.getCurUserId();
        PageResult<UserFollowing> result = userFollowingService.pageListUserFollowings(pageNo, pageSize, userId, groupId);
        return new R<>(result);
    }

    /**
     * 分页查询用户粉丝
     */
    @PostMapping("/pageListUserFans")
    public R<PageResult<UserFollowing>> pageListUserFans(@RequestParam Integer pageNo,
                                                         @RequestParam Integer pageSize) {
        Long userId = UserContext.getCurUserId();
        PageResult<UserFollowing> result = userFollowingService.pageListUserFans(pageNo, pageSize, userId);
        return new R<>(result);
    }


    /*
     * 提供远程调用的接口
     * 获取用户的粉丝列表
     */
    @GetMapping("/inner/user-fans")
    public List<UserFollowing> getUserFans(Long userId) {
        return userFollowingService.getFanList(userId);
    }


}

