package com.wz.pilipili.video.task;

import com.wz.pilipili.constant.RedisConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 处理redis相关，定时任务类
 */
@Component
public class RedisTask {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 删除 用户第一次观看视频key
     */
    @Scheduled(cron = "0 0 0 * * ?") //每天凌晨12点执行
    public void deleteUserWatchKeys() {
        Set<String> keys = redisTemplate.keys(RedisConstant.DAILY_WATCHED_VIDEO);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
