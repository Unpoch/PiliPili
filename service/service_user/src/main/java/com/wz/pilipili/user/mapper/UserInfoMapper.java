package com.wz.pilipili.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wz.pilipili.entity.user.UserInfo;

import java.util.List;

/**
 * <p>
 * 用户基本信息表 Mapper 接口
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-05-26
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * 批量更新用户信息
     */
    void batchUpdateUserInfos(List<UserInfo> userInfoList);
}
