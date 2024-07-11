package com.wz.pilipili.video.controller;

import com.wz.pilipili.context.UserContext;
import com.wz.pilipili.entity.video.Video;
import com.wz.pilipili.result.R;
import com.wz.pilipili.video.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/video/recommend")
public class VideoRecommendController {

    @Autowired
    private RecommendService recommendService;


    /**
     * 视频内容推荐（基于用户 和 内容推荐，并加入游客模式）
     */
    @GetMapping("/getVideoRecommendations")
    public R<List<Video>> getVideoRecommendations(@RequestParam String recommendType) {
        Long userId = UserContext.getCurUserId();
        List<Video> result = recommendService.getVideoRecommendations(userId, recommendType);
        return new R<>(result);
    }

}
