package com.wz.pilipili.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.user.UserMoments;

import java.util.List;

/**
 * 用户动态表 服务类
 *
 * @author unkonwnzz
 * @since 2024-05-31
 */
public interface UserMomentsService extends IService<UserMoments> {

    void pushMoments(UserMoments userMoments) throws Exception;

    List<UserMoments> getUserSubscribedMoments(Long userId);

    PageResult<UserMoments> pageListUserMoments(Integer pageNo, Integer pageSize, Long userId);
}
