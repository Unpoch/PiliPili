package com.wz.pilipili.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wz.pilipili.entity.user.RefreshTokenDetail;
import com.wz.pilipili.user.mapper.RefreshTokenMapper;
import com.wz.pilipili.user.service.RefreshTokenService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 刷新令牌记录表 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-03
 */
@Service
public class RefreshTokenServiceImpl extends ServiceImpl<RefreshTokenMapper, RefreshTokenDetail> implements RefreshTokenService {

    @Override
    public void deleteRefreshTokenByUserId(Long userId) {
        this.remove(new LambdaQueryWrapper<RefreshTokenDetail>()
                .eq(RefreshTokenDetail::getUserId, userId));
    }

    @Override
    public void addRefreshToken(Long userId, String refreshToken) {
        RefreshTokenDetail token = new RefreshTokenDetail();
        token.setUserId(userId);
        token.setRefreshToken(refreshToken);
        this.save(token);
    }

    @Override
    public RefreshTokenDetail getRefreshToken(String refreshToken) {
        return this.getOne(new LambdaQueryWrapper<RefreshTokenDetail>().eq(RefreshTokenDetail::getRefreshToken, refreshToken));
    }

}
