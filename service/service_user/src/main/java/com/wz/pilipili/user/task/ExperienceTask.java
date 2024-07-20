package com.wz.pilipili.user.task;

import com.wz.pilipili.user.service.UserInfoService;
import com.wz.pilipili.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 用户经验定时任务类
 */
@Component
public class ExperienceTask {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 每日凌晨12：00清空 用户获取的每日经验
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeDailyExperience() {
        //查询今日活跃的所有用户（上次登录时间为当前时间）
        userInfoService.batchRemoveDailyExperience();
    }
}
