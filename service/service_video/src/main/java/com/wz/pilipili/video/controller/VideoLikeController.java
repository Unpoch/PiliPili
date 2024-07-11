package com.wz.pilipili.video.controller;


import com.wz.pilipili.context.UserContext;
import com.wz.pilipili.result.R;
import com.wz.pilipili.video.service.VideoLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 视频点赞记录表 前端控制器
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-16
 */
@RestController
@RequestMapping("/video/like")
public class VideoLikeController {

    @Autowired
    private VideoLikeService videoLikeService;

    /**
     * 点赞视频
     */
    @PostMapping("/likeVideo")
    public R<String> likeVideo(@RequestParam Long videoId) {
        Long userId = UserContext.getCurUserId();
        videoLikeService.likeVideo(userId, videoId);
        return R.success();
    }

    /**
     * 取消点赞视频
     */
    @DeleteMapping("/cancelLike")
    public R<String> cancelLikeVideo(@RequestParam Long videoId) {
        Long userId = UserContext.getCurUserId();
        videoLikeService.cancelLikeVideo(userId, videoId);
        return R.success();
    }

    /**
     * 查询视频点赞数量
     * 注意：游客模式下也能观看视频
     */
    @GetMapping("/getVideoLikes")
    public R<Map<String, Object>> getVideoLikes(@RequestParam Long videoId) {
        Long userId = UserContext.getCurUserId();//游客模式下，可能为null
        Map<String, Object> result = videoLikeService.getVideoLikes(userId, videoId);
        return new R<>(result);
    }



}

