package com.wz.pilipili.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wz.pilipili.entity.user.UserCoin;
import com.wz.pilipili.user.mapper.UserCoinMapper;
import com.wz.pilipili.user.service.UserCoinService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户硬币表 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-19
 */
@Service
public class UserCoinServiceImpl extends ServiceImpl<UserCoinMapper, UserCoin> implements UserCoinService {

    /**
     * 获取用户硬币数量
     */
    @Override
    public Integer getUserCoins(Long userId) {
        UserCoin userCoin = baseMapper.selectOne(new LambdaQueryWrapper<UserCoin>()
                .eq(UserCoin::getUserId, userId)
                .select(UserCoin::getAmount));
        return userCoin.getAmount();
    }

    /**
     * 更新用户硬币数量
     */
    @Override
    public void updateUserCoin(Long userId, Integer amount) {
        UserCoin userCoin = new UserCoin();
        userCoin.setUserId(userId);
        userCoin.setAmount(amount);
        baseMapper.update(userCoin, new LambdaQueryWrapper<UserCoin>()
                .eq(UserCoin::getUserId, userId));
    }

    /**
     * 增加用户硬币数量
     */
    @Override
    public void increaseCoins(Long userId, Integer amount) {
        UserCoin userCoin = baseMapper.selectOne(new LambdaQueryWrapper<UserCoin>()
                .eq(UserCoin::getUserId, userId));
        userCoin.setAmount(userCoin.getAmount() + amount);
        baseMapper.updateById(userCoin);
    }
}
