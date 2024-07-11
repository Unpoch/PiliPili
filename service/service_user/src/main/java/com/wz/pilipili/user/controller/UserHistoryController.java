package com.wz.pilipili.user.controller;

import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.result.R;
import com.wz.pilipili.user.service.UserHistoryService;
import com.wz.pilipili.util.DateUtil;
import com.wz.pilipili.vo.user.UserVideoHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 用户历史记录
 */
@RestController
@RequestMapping("/user/history")
public class UserHistoryController {

    @Autowired
    private UserHistoryService userHistoryService;

    /**
     * 分页获取用户观看历史
     */
    @GetMapping("/pageListVideoViewHistory")
    public R<PageResult<UserVideoHistory>> pageListVideoViewHistory(@RequestParam Integer pageNo,
                                                                    @RequestParam Integer pageSize,
                                                                    @RequestParam Long userId) {
        PageResult<UserVideoHistory> result = userHistoryService.pageListVideoViewHistory(pageNo, pageSize, userId);
        return new R<>(result);
    }

    /**
     * 分页查询用户最近（14天内）投币视频历史记录
     */
    @GetMapping("/pageListVideoCoinHistory")
    public R<PageResult<UserVideoHistory>> pageListVideoCoinHistory(@RequestParam Integer pageNo,
                                                                    @RequestParam Integer pageSize,
                                                                    @RequestParam Long userId) {
        Date now = new Date();
        String endTime = DateUtil.dateFormat(now);
        String startTime = DateUtil.minus(now, 14, DateUtil.TimeUnit.DAY);
        PageResult<UserVideoHistory> result = userHistoryService.pageListVideoCoinHistory(pageNo, pageSize, userId, startTime, endTime);
        return new R<>(result);
    }

    /**
     * 分页获取用户最近（7天内）点赞视频历史记录
     */
    @GetMapping("/pageListVideoLikeHistory")
    public R<PageResult<UserVideoHistory>> pageListVideoLikeHistory(@RequestParam Integer pageNo,
                                                                    @RequestParam Integer pageSize,
                                                                    @RequestParam Long userId) {

        Date now = new Date();
        String endTime = DateUtil.dateFormat(now);
        String startTime = DateUtil.minus(now, 7, DateUtil.TimeUnit.DAY);
        PageResult<UserVideoHistory> result = userHistoryService.pageListVideoLikeHistory(pageNo, pageSize, userId, startTime, endTime);
        return new R<>(result);
    }

}
