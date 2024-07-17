package com.wz.pilipili.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.user.UserCoin;

/**
 * <p>
 * 用户硬币表 服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-19
 */
public interface UserCoinService extends IService<UserCoin> {

    Integer getUserCoins(Long userId);

    void updateUserCoin(Long userId, Integer amount);

    void increaseCoins(Long userId, Integer amount);
}
