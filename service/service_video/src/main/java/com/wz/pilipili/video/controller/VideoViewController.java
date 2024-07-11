package com.wz.pilipili.video.controller;


import com.wz.pilipili.context.UserContext;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.video.Video;
import com.wz.pilipili.entity.video.VideoView;
import com.wz.pilipili.result.R;
import com.wz.pilipili.video.service.VideoViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 视频观看记录表 前端控制器
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-17
 */
@RestController
@RequestMapping("/video/view")
public class VideoViewController {

    @Autowired
    private VideoViewService videoViewService;

    /**
     * 添加视频播放记录
     */
    @PostMapping("/addVideoView")
    public R<String> addVideoView(@RequestBody VideoView videoView, HttpServletRequest request) {
        videoView.setUserId(UserContext.getCurUserId());
        videoViewService.addVideoView(videoView, request);
        return R.success();
    }

    /**
     * 查询视频播放量
     */
    @GetMapping("/getViewCounts")
    public R<Integer> getViewCounts(@RequestParam Long videoId) {
        Integer count = videoViewService.getViewCounts(videoId);
        return new R<>(count);
    }



}

