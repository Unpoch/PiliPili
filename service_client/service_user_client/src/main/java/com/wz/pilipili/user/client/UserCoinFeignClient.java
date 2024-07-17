package com.wz.pilipili.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "service-user", url = "/user/coin")
public interface UserCoinFeignClient {


    @GetMapping("/inner/getUserCoins")
    public Integer getUserCoins(@RequestParam Long userId);

    /*
    远程调用接口：更新用户硬币数量
     */
    @PostMapping("/inner/updateUserCoin")
    public void updateUserCoin(Long userId, Integer amount);

    /*
    远程调用接口：增加用户硬币数量
     */
    @PostMapping("/inner/increaseCoins")
    public void increaseCoins(@RequestParam Long userId,@RequestParam Integer amount);

}
