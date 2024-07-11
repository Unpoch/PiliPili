package com.wz.pilipili.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wz.pilipili.entity.user.UserInfo;
import com.wz.pilipili.user.mapper.UserInfoMapper;
import com.wz.pilipili.user.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 用户基本信息表 服务实现类
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    /**
     * 根据userId获取用户信息UserInfo
     */
    @Override
    public UserInfo getUserInfoByUserId(Long userId) {
        return this.getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUserId, userId));
    }

    /**
     * 根据userIds集合 获取其对应的用户信息UserInfo集合
     */
    @Override
    public List<UserInfo> getUserInfoListByUserIds(Set<Long> userIds) {
        return this.list(new LambdaQueryWrapper<UserInfo>().in(UserInfo::getUserId, userIds));
    }


    /**
     * 根据分页参数 和 查询条件 分页查询用户列表返回
     */
    @Override
    public IPage<UserInfo> pageListUserInfos(JSONObject params) {
        //1.获取分页参数 和 查询条件
        String nick = params.getString("nick");
        Integer start = params.getInteger("start");
        Integer limit = params.getInteger("limit");
        //2.创建Page对象，分页查询
        Page<UserInfo> pageParam = new Page<>(start, limit);//分页参数
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<UserInfo>()
                .like(UserInfo::getNick, nick).orderByDesc(UserInfo::getId);
        return baseMapper.selectPage(pageParam, wrapper);
    }
}
