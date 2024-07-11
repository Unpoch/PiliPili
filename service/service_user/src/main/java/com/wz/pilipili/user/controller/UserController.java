package com.wz.pilipili.user.controller;


import com.alibaba.fastjson.JSONObject;
import com.wz.pilipili.context.UserContext;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.user.User;
import com.wz.pilipili.entity.user.UserInfo;
import com.wz.pilipili.result.R;
import com.wz.pilipili.user.service.UserFollowingService;
import com.wz.pilipili.user.service.UserService;
import com.wz.pilipili.util.RSAUtil;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-05-26
 */
@RestController
@RequestMapping("/user/userinfo")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserFollowingService userFollowingService;

    /**
     * 返回RSA公钥
     */
    @GetMapping("/rsa-pks")
    public R<String> getRSAPublicKey() {
        String publicKey = RSAUtil.getPublicKeyStr();
        return new R<>(publicKey);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public R<String> register(@RequestBody User user) {
        userService.register(user);
        return R.success();
    }

    /**
     * 登录
     * 登录成功后会返回token（accessToken）
     */
    @PostMapping("/login")
    public R<String> login(@RequestBody User user) throws Exception {
        String token = userService.login(user);
        return R.success(token);
    }

    /**
     * 双令牌 实现登录升级
     * accessToken和refreshToken
     */
    @PostMapping("/login-dts") //double tokens
    public R<Map<String, Object>> loginForDts(@RequestBody User user) throws Exception {
        Map<String, Object> map = userService.loginForDts(user);
        return new R<>(map);
    }

    /**
     * 退出登录
     * 实际上就是删除refreshToken
     */
    @DeleteMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        Long userId = UserContext.getCurUserId();
        userService.logout(userId);
        return R.success();
    }

    /**
     * 刷新token
     */
    @PostMapping("/access-token")
    public R<String> refreshAccessToken(HttpServletRequest request) throws Exception {
        String refreshToken = request.getHeader("refreshToken");
        String accessToken = userService.refreshAccessToken(refreshToken);
        return new R<>(accessToken);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/user-info")
    public R<User> getUserInfo() {
        Long userId = UserContext.getCurUserId();
        User user = userService.getUserInfo(userId);
        return new R<>(user);
    }

    /**
     * 更新用户
     */
    @PutMapping("/users")
    public R<String> updateUser(@RequestBody User user) throws Exception {
        Long userId = UserContext.getCurUserId();
        user.setId(userId);
        userService.updateUser(user);
        return R.success();
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/user-infos")
    public R<String> updateUserInfo(@RequestBody UserInfo userInfo) {
        Long userId = UserContext.getCurUserId();
        userInfo.setUserId(userId);
        userService.updateUserInfo(userInfo);
        return R.success();
    }


    /**
     * 分页查询用户
     * nick参数用于模糊查询条件
     */
    @GetMapping("/user-page-list")
    public R<PageResult<UserInfo>> pageListUserInfos(@RequestParam Integer no, @RequestParam Integer size, String nick) {
        //1.封装参数 获取PageResult对象(分页查询用户列表)
        Long userId = UserContext.getCurUserId();
        JSONObject params = new JSONObject();
        params.put("no", no);
        params.put("size", size);
        params.put("nick", nick);
        params.put("userId", userId);
        PageResult<UserInfo> pageResult = userService.pageListUserInfos(params);
        //2.查询出来的用户，还需要判断是否被当前登录的用户关注
        if (pageResult.getTotal() > 0) {
            List<UserInfo> checkUserInfoList = userFollowingService.checkFollowingStatus(pageResult.getList(), userId);
            pageResult.setList(checkUserInfoList);//设置为检查过关注关系的列表
        }
        return new R<>(pageResult);
    }



    /*
    远程调用接口
    根据userId集合，批量查询用户信息
     */
    @GetMapping("/inner/batchGetUserInfo")
    public List<UserInfo> batchGetUserInfoListByUserIds(Set<Long> userIds) {
        return userService.batchGetUserInfoListByUserIds(userIds);
    }

    /*
    远程调用接口
    根据userId查询用户信息
     */
    @GetMapping("/inner/getInfoById")
    public UserInfo getInfoById(Long userId) {
        return userService.getUserInfoByUserId(userId);
    }

}

