package com.wz.pilipili.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.video.*;
import com.wz.pilipili.vo.user.UserVideoHistory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * <p>
 * 视频投稿记录表 服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-15
 */
public interface VideoService extends IService<Video> {

    void postVideo(Video video);

    PageResult<Video> pageListVideos(Integer size, Integer no, String area);

    void playVideo(HttpServletRequest request, HttpServletResponse response,Integer fileId) throws Exception;

    Video getVideoByVideoId(Long videoId);

    List<Video> getVideoListByVideoIds(Set<Long> videoIds);

    Long addTag(Tag tag);

    void deleteVideoTags(List<Long> tagIdList, Long videoId);

    List<Tag> getVideoTags(Long videoId);

    void addVideoCoins(Long userId, VideoCoin videoCoin);

    Map<String, Object> getVideoCoins(Long userId, Long videoId);

    Map<String, Object> getVideoDetails(Long videoId);

    List<Video> getVideoListByUserId(Long userId);

    PageResult<Video> pageListUserVideos(Integer pageNo, Integer pageSize, Long userId);

    PageResult<UserVideoHistory> pageListVideoViewHistory(Integer pageNo, Integer pageSize, Long userId);

    PageResult<UserVideoHistory> pageListVideoLikeHistory(Integer pageNo, Integer pageSize, Long userId, String startTime,String endTime);

    PageResult<UserVideoHistory> pageListVideoCoinHistory(Integer pageNo, Integer pageSize, Long userId, String startTime,String endTime);

    Integer getUserVideoLikes(Long userId);

    PageResult<Video> pageListAreaVideo(Integer pageNo, Integer pageSize, String area);

    List<Area> getAllAreas();

    String shareVideo(Long userId, Long videoId);

    Map<String, Object> getVideoShareCount(Long userId, Long videoId);
}
