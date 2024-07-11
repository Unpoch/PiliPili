package com.wz.pilipili.video.controller;


import com.wz.pilipili.context.UserContext;
import com.wz.pilipili.entity.video.Danmu;
import com.wz.pilipili.result.R;
import com.wz.pilipili.video.service.DanmuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 弹幕记录表 前端控制器
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-22
 */
@RestController
@RequestMapping("/video/danmu")
public class DanmuController {

    @Autowired
    private DanmuService danmuService;

    /**
     * 获取视频弹幕
     */
    @GetMapping("/getVideoDanmus")
    public R<List<Danmu>> getVideoDanmus(@RequestParam Long videoId,
                                         String startTime,
                                         String endTime) throws Exception {
        //需要判断当前是登录模式还是游客模式。登录模式允许用户对时间段进行筛选，游客模式则不允许
        Long userId = UserContext.getCurUserId();
        startTime = userId != null ? startTime : null;
        endTime = userId != null ? endTime : null;
        List<Danmu> result = danmuService.getDanmus(videoId, startTime, endTime);
        return new R<>(result);
    }

}

