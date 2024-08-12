package com.wz.pilipili.user.controller;


import com.wz.pilipili.context.UserContext;
import com.wz.pilipili.entity.user.UserCoin;
import com.wz.pilipili.result.R;
import com.wz.pilipili.user.service.UserCoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户硬币表 前端控制器
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-19
 */
@RestController
@RequestMapping("/user/coin")
public class UserCoinController {

    @Autowired
    private UserCoinService userCoinService;


    /**
     * 查询用户硬币数量
     */
    @GetMapping("/getUserCoins")
    public R<Integer> getUserCoins() {
        Long userId = UserContext.getCurUserId();
        Integer count = userCoinService.getUserCoins(userId);
        return new R<>(count);
    }


    /*
    远程调用接口：获取用户硬币数量
     */
    @GetMapping("/inner/getUserCoins")
    public Integer getUserCoins(@RequestParam Long userId) {
        return userCoinService.getUserCoins(userId);
    }

    /*
    远程调用接口：更新用户硬币数量
     */
    @PostMapping("/inner/updateUserCoin")
    public void updateUserCoin(Long userId, Integer amount) {
        userCoinService.updateUserCoin(userId, amount);
    }

    /*
    远程调用接口：增加用户硬币数量
     */
    @PostMapping("/inner/increaseCoins")
    public void increaseCoins(@RequestParam Long userId,@RequestParam Integer amount) {
        userCoinService.increaseCoins(userId, amount);
    }
}

