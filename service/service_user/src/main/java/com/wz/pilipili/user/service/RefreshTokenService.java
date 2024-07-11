package com.wz.pilipili.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.user.RefreshTokenDetail;

/**
 * <p>
 * 刷新令牌记录表 服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-03
 */
public interface RefreshTokenService extends IService<RefreshTokenDetail> {

    void deleteRefreshTokenByUserId(Long userId);

    void addRefreshToken(Long userId, String refreshToken);

    RefreshTokenDetail getRefreshToken(String refreshToken);

}
