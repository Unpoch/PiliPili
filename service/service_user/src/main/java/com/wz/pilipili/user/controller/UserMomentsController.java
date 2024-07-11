package com.wz.pilipili.user.controller;


import com.wz.pilipili.annotation.ApiLimitedRole;
import com.wz.pilipili.annotation.DataLimited;
import com.wz.pilipili.constant.AuthRoleConstant;
import com.wz.pilipili.context.UserContext;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.user.UserMoments;
import com.wz.pilipili.result.R;
import com.wz.pilipili.user.service.UserMomentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户动态表 前端控制器
 *
 * @author unkonwnzz
 * @since 2024-05-31
 */
@RestController
@RequestMapping("/user/moments")
public class UserMomentsController {

    @Autowired
    private UserMomentsService userMomentsService;

    /**
     * 用户发布动态
     */
    //被限制的角色的编码，该角色不允许调用该接口
    @ApiLimitedRole(limitedRoleCodeList = {AuthRoleConstant.ROLE_LV0})
    @DataLimited
    @PostMapping("/user-moments")
    public R<String> pushMoments(@RequestBody UserMoments userMoments) throws Exception {
        Long userId = UserContext.getCurUserId();
        userMoments.setUserId(userId);
        userMomentsService.pushMoments(userMoments);
        return R.success();
    }

    /**
     * 用户查询订阅内容的动态
     */
    @GetMapping("/user-subscribed-moments")
    public R<List<UserMoments>> getUserSubscribedMoments() {
        Long userId = UserContext.getCurUserId();
        List<UserMoments> userMomentsList = userMomentsService.getUserSubscribedMoments(userId);
        return new R<>(userMomentsList);
    }

    /**
     * 用户分页查询发布的动态
     */
    @GetMapping("/pageListUserMoments")
    public R<PageResult<UserMoments>> pageListUserMoments(@RequestParam Integer pageNo,
                                                          @RequestParam Integer pageSize) {
        Long userId = UserContext.getCurUserId();
        PageResult<UserMoments> pageResult = userMomentsService.pageListUserMoments(pageNo, pageSize, userId);
        return new R<>(pageResult);
    }

}

