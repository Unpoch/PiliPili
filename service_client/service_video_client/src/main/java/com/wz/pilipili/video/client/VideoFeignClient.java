package com.wz.pilipili.video.client;

import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.video.Video;
import com.wz.pilipili.vo.user.UserVideoHistory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@FeignClient(value = "service-video",url = "/video")
public interface VideoFeignClient {

    /**
     * 分页查询用户投稿视频
     */
    @GetMapping("/inner/pageListUserVideos")
    public PageResult<Video> pageListUserVideos(@RequestParam Integer pageNo,
                                                @RequestParam Integer pageSize,
                                                @RequestParam Long userId);

    /**
     * 获取用户投稿视频点赞总数
     */
    @GetMapping("/inner/getUserVideoLikes")
    public Integer getUserVideoLikes(@RequestParam Long userId);

    /**
    分页查询用户视频播放记录
     */
    @GetMapping("/inner/pageListVideoViewHistory")
    public PageResult<UserVideoHistory> pageListVideoViewHistory(@RequestParam Integer pageNo,
                                                      @RequestParam Integer pageSize,
                                                      @RequestParam Long userId);

    /**
    分页查询用户最近点赞视频记录
     */
    @GetMapping("/inner/pageListVideoLikeHistory")
    public PageResult<UserVideoHistory> pageListVideoLikeHistory(@RequestParam Integer pageNo,
                                                      @RequestParam Integer pageSize,
                                                      @RequestParam Long userId,
                                                      String startTime,
                                                      String endTime);

    /**
    分页查询用户最近投币视频记录
     */
    @GetMapping("/inner/pageListVideoCoinHistory")
    public PageResult<UserVideoHistory> pageListVideoCoinHistory(@RequestParam Integer pageNo,
                                                                 @RequestParam Integer pageSize,
                                                                 @RequestParam Long userId,
                                                                 String startTime,
                                                                 String endTime);
}
