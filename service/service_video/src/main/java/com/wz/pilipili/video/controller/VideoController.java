package com.wz.pilipili.video.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wz.pilipili.context.UserContext;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.video.*;
import com.wz.pilipili.result.R;
import com.wz.pilipili.search.client.SearchFeignClient;
import com.wz.pilipili.video.service.VideoService;
import com.wz.pilipili.vo.user.UserVideoHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 视频投稿记录表 前端控制器
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-15
 */
@RestController
@RequestMapping("/video")
public class VideoController {


    @Autowired
    private VideoService videoService;

    @Autowired
    private SearchFeignClient searchFeignClient;

    /**
     * 视频投稿
     */
    @PostMapping("/postVideo")
    public R<String> postVideo(@RequestBody Video video) {
        Long userId = UserContext.getCurUserId();
        video.setUserId(userId);
        videoService.postVideo(video);
        searchFeignClient.addVideo(video);
        return R.success();
    }

    /**
     * 瀑布流视频列表（分页查询视频列表）
     */
    @GetMapping("/pageVideo")
    public R<PageResult<Video>> pageListVideos(Integer size, Integer no, String area) {
        PageResult<Video> result = videoService.pageListVideos(size, no, area);
        return new R<>(result);
    }

    /**
     * 以流的形式返回，流的内容写在响应中，因此不需要R<>
     */
    @GetMapping("/playVideo")
    public void playVideo(HttpServletRequest request,
                          HttpServletResponse response,
                          Integer fileId) throws Exception {
        videoService.playVideo(request, response, fileId);
    }


    /**
     * 添加视频标签
     */
    @PostMapping("/tag/addTag")
    public R<Long> addTag(@RequestBody Tag tag) {
        Long tagId = videoService.addTag(tag);
        return new R<>(tagId);
    }

    /**
     * 删除视频标签
     */
    @PostMapping("/tag/deleteVideoTags")
    public R<String> deleteVideoTags(@RequestBody JSONObject params) {
        String tagIdList = params.getString("tagIdList");//获取tagId集合
        Long videoId = params.getLong("videoId");
        videoService.deleteVideoTags(JSONArray.parseArray(tagIdList).toJavaList(Long.class), videoId);
        return R.success();
    }


    /**
     * 查询视频标签
     */
    @GetMapping("/tag/getVideoTags")
    public R<List<Tag>> getVideoTags(@RequestParam Long videoId) {
        List<Tag> videoTags = videoService.getVideoTags(videoId);
        return new R<>(videoTags);
    }

    /**
     * 视频投币
     */
    @PostMapping("/coin/addVideoCoins")
    public R<String> addVideoCoins(@RequestBody VideoCoin videoCoin) {
        Long userId = UserContext.getCurUserId();
        videoService.addVideoCoins(userId, videoCoin);
        return R.success();
    }

    /**
     * 查询视频硬币数量
     */
    @GetMapping("/coin/getVideoCoins")
    public R<Map<String, Object>> getVideoCoins(@RequestParam Long videoId) {
        Long userId = UserContext.getCurUserId();
        Map<String, Object> result = videoService.getVideoCoins(userId, videoId);
        return new R<>(result);
    }

    /**
     * 视频详情
     */
    @GetMapping("/getVideoDetails")
    public R<Map<String, Object>> getVideoDetails(@RequestParam Long videoId) {
        Map<String, Object> result = videoService.getVideoDetails(videoId);
        return new R<>(result);
    }

    /**
     * 查询所有分区（有多少分区）
     */
    @GetMapping("/area/getAllAreas")
    public R<List<Area>> getAllAreas() {
        List<Area> result = videoService.getAllAreas();
        return new R<>(result);
    }

    /**
     * 分页查询某个分区下所有视频
     */
    @GetMapping("/area/pageListAreaVideo")
    public R<PageResult<Video>> pageListAreaVideo(@RequestParam Integer pageNo,
                                                   @RequestParam Integer pageSize,
                                                   @RequestParam String area) {
        PageResult<Video> pageResult = videoService.pageListAreaVideo(pageNo, pageSize, area);
        return new R<>(pageResult);
    }

    /*
     * 远程调用
     * 根据用户id获取其获取其视频的点赞数
     */
    @GetMapping("/inner/getUserVideoLikes")
    public Integer getUserVideoLikes(@RequestParam Long userId) {
        return videoService.getUserVideoLikes(userId);
    }

    /*
    远程调用接口
    分页查询用户投稿视频
     */
    @GetMapping("/inner/pageListUserVideos")
    public PageResult<Video> pageListUserVideos(@RequestParam Integer pageNo,
                                                @RequestParam Integer pageSize,
                                                @RequestParam Long userId) {
        return videoService.pageListUserVideos(pageNo, pageSize, userId);
    }

    /*
    远程调用接口
    分页查询用户视频播放记录
     */
    @GetMapping("/inner/pageListVideoViewHistory")
    public PageResult<UserVideoHistory> pageListVideoViewHistory(@RequestParam Integer pageNo,
                                                                 @RequestParam Integer pageSize,
                                                                 @RequestParam Long userId) {
        return videoService.pageListVideoViewHistory(pageNo, pageSize, userId);
    }

    /*
    远程调用接口
    分页查询用户最近点赞视频记录
     */
    @GetMapping("/inner/pageListVideoLikeHistory")
    public PageResult<UserVideoHistory> pageListVideoLikeHistory(@RequestParam Integer pageNo,
                                                      @RequestParam Integer pageSize,
                                                      @RequestParam Long userId,
                                                      String startTime,
                                                      String endTime) {
        return videoService.pageListVideoLikeHistory(pageNo, pageSize, userId, startTime, endTime);
    }

    /*
    远程调用接口
    分页查询用户最近投币视频记录
     */
    @GetMapping("/inner/pageListVideoCoinHistory")
    public PageResult<UserVideoHistory> pageListVideoCoinHistory(@RequestParam Integer pageNo,
                                                      @RequestParam Integer pageSize,
                                                      @RequestParam Long userId,
                                                      String startTime,
                                                      String endTime) {
        return videoService.pageListVideoCoinHistory(pageNo, pageSize, userId, startTime, endTime);
    }
}

