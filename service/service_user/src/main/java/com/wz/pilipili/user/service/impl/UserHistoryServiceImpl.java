package com.wz.pilipili.user.service.impl;

import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.user.service.UserHistoryService;
import com.wz.pilipili.video.client.VideoFeignClient;
import com.wz.pilipili.vo.user.UserVideoHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 用户历史记录服务类
 */
@Service
public class UserHistoryServiceImpl implements UserHistoryService {


    @Autowired
    private VideoFeignClient videoFeignClient;

    /**
     * 分页获取用户观看视频记录
     */
    @Override
    public PageResult<UserVideoHistory> pageListVideoViewHistory(Integer pageNo, Integer pageSize, Long userId) {
        return videoFeignClient.pageListVideoViewHistory(pageNo, pageSize, userId);
    }

    /**
     * 分页获取用户最近点赞视频记录
     */
    @Override
    public PageResult<UserVideoHistory> pageListVideoLikeHistory(Integer pageNo, Integer pageSize, Long userId, String startTime, String endTime) {
        return videoFeignClient.pageListVideoLikeHistory(pageNo, pageSize, userId, startTime, endTime);
    }

    /**
     * 分页获取用户最近投币视频记录
     */
    @Override
    public PageResult<UserVideoHistory> pageListVideoCoinHistory(Integer pageNo, Integer pageSize, Long userId, String startTime, String endTime) {
        return videoFeignClient.pageListVideoCoinHistory(pageNo, pageSize, userId, startTime, endTime);
    }
}
