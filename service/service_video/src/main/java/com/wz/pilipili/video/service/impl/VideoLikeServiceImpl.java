package com.wz.pilipili.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.video.Video;
import com.wz.pilipili.entity.video.VideoLike;
import com.wz.pilipili.exception.ConditionException;
import com.wz.pilipili.video.mapper.VideoLikeMapper;
import com.wz.pilipili.video.service.VideoLikeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wz.pilipili.video.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 视频点赞记录表 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-16
 */
@Service
public class VideoLikeServiceImpl extends ServiceImpl<VideoLikeMapper, VideoLike> implements VideoLikeService {


    @Autowired
    private VideoLikeMapper videoLikeMapper;

    /**
     * 点赞视频
     */
    @Override
    public void likeVideo(Long userId, Long videoId) {
        // //1.根据videoId查询视频是否存在
        // Video video = videoService.getVideoByVideoId(videoId);
        // if (video == null) {
        //     throw new ConditionException("非法视频！");
        // }
        //2.根据videoId和userId查询VideoLike，判断是否已经点赞过该视频
        VideoLike videoLike = this.getVideoLikeByUserIdAndVideoId(userId, videoId);
        if (videoLike == null) {
            throw new ConditionException("你已经点赞过该视频！");
        }
        //3.如果没有点赞过，创建VideoLike对象，插入数据库
        videoLike = new VideoLike();
        videoLike.setVideoId(videoId);
        videoLike.setUserId(userId);
        baseMapper.insert(videoLike);
    }

    /**
     * 取消点赞视频
     */
    @Override
    public void cancelLikeVideo(Long userId, Long videoId) {
        //调用根据userId和videoId删除点赞记录的方法
        baseMapper.delete(new LambdaQueryWrapper<VideoLike>()
                .eq(VideoLike::getUserId, userId)
                .eq(VideoLike::getVideoId, videoId));
    }

    /**
     * 获取视频点赞数量
     */
    @Override
    public Map<String, Object> getVideoLikes(Long userId, Long videoId) {
        //1.通过videoId获取视频点赞数量
        int count = this.count(new LambdaQueryWrapper<VideoLike>().eq(VideoLike::getVideoId, videoId));
        //2.根据videoId和userId查询VideoLike对象，判断自己是否已经赞过
        VideoLike videoLike = this.getVideoLikeByUserIdAndVideoId(userId, videoId);
        boolean like = videoLike == null;//是否赞过
        //3.封装map返回。两个字段：点赞数量count，是否赞过视频like（前端需要根据这个字段进行点赞特效的展示）
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("like", like);
        return result;
    }

    @Override
    public PageResult<VideoLike> pageListVideoLikes(Integer pageNo, Integer pageSize, Long userId, String startTime,String endTime) {
        int start = (pageNo - 1) * pageSize;
        Page<VideoLike> pageParams = new Page<>(start, pageSize);
        IPage<VideoLike> page = baseMapper.selectPage(pageParams, new LambdaQueryWrapper<VideoLike>()
                .eq(VideoLike::getUserId, userId)
                .ge(VideoLike::getCreateTime, startTime)
                .le(VideoLike::getCreateTime, endTime)
                .orderByDesc(VideoLike::getId));
        return new PageResult<>(page.getTotal(),page.getRecords());
    }

    /**
     * 根据videoId集合获取视频点赞总数
     */
    @Override
    public Integer getVideoLikesByVideoIds(Set<Long> videoIds) {
        return videoLikeMapper.getVideoLikesByVideoIds(videoIds);
    }


    /*
    根据userId和videoId获取VideoLike
     */
    private VideoLike getVideoLikeByUserIdAndVideoId(Long userId, Long videoId) {
        return baseMapper.selectOne(new LambdaQueryWrapper<VideoLike>()
                .eq(VideoLike::getUserId, userId)
                .eq(VideoLike::getVideoId, videoId));
    }
}
