package com.wz.pilipili.user.service;

import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.video.Video;
import com.wz.pilipili.vo.user.UserVideoHistory;

import java.util.Date;

public interface UserHistoryService {
    PageResult<UserVideoHistory> pageListVideoViewHistory(Integer pageNo, Integer pageSize, Long userId);

    PageResult<UserVideoHistory> pageListVideoLikeHistory(Integer pageNo, Integer pageSize, Long userId, String startTime, String endTime);

    PageResult<UserVideoHistory> pageListVideoCoinHistory(Integer pageNo, Integer pageSize, Long userId, String startTime, String endTime);
}
