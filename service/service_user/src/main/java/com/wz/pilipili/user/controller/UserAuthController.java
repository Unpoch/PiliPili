package com.wz.pilipili.user.controller;

import com.wz.pilipili.context.UserContext;
import com.wz.pilipili.entity.auth.UserAuthorities;
import com.wz.pilipili.result.R;
import com.wz.pilipili.user.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/auth")
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    /**
     * 获取当前用户权限
     */
    @GetMapping("/user-authorities")
    public R<UserAuthorities> getUserAuthorities() {
        Long userId = UserContext.getCurUserId();
        UserAuthorities userAuthorities = userAuthService.getUserAuthorities(userId);
        return new R<>(userAuthorities);
    }

}
