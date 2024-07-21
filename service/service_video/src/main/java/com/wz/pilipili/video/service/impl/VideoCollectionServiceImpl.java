package com.wz.pilipili.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wz.pilipili.constant.UserConstant;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.video.Video;
import com.wz.pilipili.entity.video.VideoCollection;
import com.wz.pilipili.entity.video.VideoCollectionGroup;
import com.wz.pilipili.exception.ConditionException;
import com.wz.pilipili.user.client.UserCoinFeignClient;
import com.wz.pilipili.video.mapper.VideoCollectionMapper;
import com.wz.pilipili.video.service.VideoCollectionGroupService;
import com.wz.pilipili.video.service.VideoCollectionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wz.pilipili.video.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 视频收藏记录表 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-17
 */
@Service
public class VideoCollectionServiceImpl extends ServiceImpl<VideoCollectionMapper, VideoCollection> implements VideoCollectionService {

    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoCollectionGroupService videoCollectionGroupService;

    @Autowired
    private UserCoinFeignClient userCoinFeignClient;

    /**
     * 收藏视频
     */
    @Transactional
    @Override
    public void collectVideo(Long userId, VideoCollection videoCollection) {
        //1.获取videoId和groupId，进行参数校验
        Long videoId = videoCollection.getVideoId();
        Long groupId = videoCollection.getGroupId();
        if (videoId == null || groupId == null) {
            throw new ConditionException("参数异常！");
        }
        //2.根据videoId获取视频，视频判断
        Video video = videoService.getVideoByVideoId(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！");
        }
        //3.删除原有视频收藏，添加新的视频收藏（有更新的效果）
        baseMapper.delete(new LambdaQueryWrapper<VideoCollection>()
                .eq(VideoCollection::getUserId, userId)
                .eq(VideoCollection::getVideoId, videoId));
        videoCollection.setUserId(userId);
        baseMapper.insert(videoCollection);
        //4.判断此时该视频的收藏数 % 200 == 0，则说明视频的收藏数增加了200，增加投稿人的硬币数
        int collectCount = baseMapper.selectCount(new LambdaQueryWrapper<VideoCollection>()
                .eq(VideoCollection::getVideoId, videoId));
        if (collectCount % 200 == 0) {//增加投稿用户1硬币
            userCoinFeignClient.increaseCoins(userId, UserConstant.ONE_COIN);
        }
    }

    /**
     * 更新收藏视频（换收藏夹）
     */
    @Transactional
    @Override
    public void updateVideoCollection(Long userId, VideoCollection videoCollection) {
        //1.获取videoId和groupId，进行参数校验
        Long videoId = videoCollection.getVideoId();
        Long groupId = videoCollection.getGroupId();
        if (videoId == null || groupId == null) {
            throw new ConditionException("参数异常！");
        }
        //2.根据videoId获取视频，视频判断
        Video video = videoService.getVideoByVideoId(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！");
        }
        //3.将userId设置到VideoCollection中，调用更新的方法（更新这条记录的groupId）
        // videoCollection.setUserId(userId);
        baseMapper.update(videoCollection, new LambdaQueryWrapper<VideoCollection>()
                .eq(VideoCollection::getUserId, userId)
                .eq(VideoCollection::getVideoId, videoId));
    }

    /**
     * 取消收藏
     */
    @Override
    public void cancelCollectVideo(Long userId, Long videoId) {
        //调用删除的方法
        baseMapper.delete(new LambdaQueryWrapper<VideoCollection>()
                .eq(VideoCollection::getUserId, userId)
                .eq(VideoCollection::getVideoId, videoId));
    }

    /**
     * 获取视频收藏
     */
    @Override
    public Map<String, Object> getVideoCollections(Long userId, Long videoId) {
        //1.根据videoId获取视频收藏数量
        int count = baseMapper.selectCount(new LambdaQueryWrapper<VideoCollection>()
                .eq(VideoCollection::getVideoId, videoId));
        //2.根据userId和videoId获取VideoCollection
        VideoCollection videoCollection = baseMapper.selectOne(new LambdaQueryWrapper<VideoCollection>()
                .eq(VideoCollection::getUserId, userId)
                .eq(VideoCollection::getVideoId, videoId));
        //3.封装count和collected字段（是否已经收藏）
        boolean collected = videoCollection == null;
        HashMap<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("collected", collected);
        return result;
    }

    /**
     * 添加收藏夹
     */
    @Override
    public void addVideoCollectionGroup(Long userId, VideoCollectionGroup videoCollectionGroup) {
        videoCollectionGroupService.addVideoCollectionGroup(userId, videoCollectionGroup);
    }

    /**
     * 删除收藏夹
     */
    @Transactional
    @Override
    public void deleteCollectionGroup(Long userId, Long groupId) {
        //1.删除CollectionGroup这条记录
        videoCollectionGroupService.deleteCollectionGroup(userId, groupId);
        //2.删除收藏夹下的所有视频记录（t_video_collection和该收藏夹相关记录）
        baseMapper.delete(new LambdaQueryWrapper<VideoCollection>()
                .eq(VideoCollection::getGroupId, groupId)
                .eq(VideoCollection::getUserId, userId));
    }

    /**
     * 更新收藏夹
     */
    @Override
    public void updateCollectionGroup(Long userId, VideoCollectionGroup videoCollectionGroup) {
        videoCollectionGroupService.updateCollectionGroup(userId, videoCollectionGroup);
    }

    /**
     * 初始化用户默认收藏夹
     */
    @Override
    public void addDefaultVideoCollectionGroup(Long userId) {
        videoCollectionGroupService.addDefaultVideoCollectionGroup(userId);
    }

    /**
     * 分页获取收藏夹的所有视频信息
     */
    @Override
    public PageResult<VideoCollection> pageGroupVideos(Integer no, Integer size, Integer groupId, Long userId) {
        //1.封装分页参数
        int start = (no - 1) * size;
        int limit = size;
        //2.分页查询，查询条件：groupId和userId
        Page<VideoCollection> pageParams = new Page<>(start, limit);
        IPage<VideoCollection> page = baseMapper.selectPage(pageParams, new LambdaQueryWrapper<VideoCollection>()
                .eq(VideoCollection::getUserId, userId)
                .eq(VideoCollection::getGroupId, groupId)
                .orderByDesc(VideoCollection::getId));
        //3.取出record列表，封装VideoCollection的Video字段
        //具体需要先获取videoId集合，根据videoId获取视频信息 -> 这里我们批量获取所有视频集合，然后进行匹配
        //（还有一种方案：遍历record列表，取出每一个元素的videoId，然后调用查询方法查询对应Video，然后进行设置）
        List<VideoCollection> videoCollectionList = page.getRecords();
        Set<Long> videoIds = videoCollectionList.stream().map(VideoCollection::getVideoId).collect(Collectors.toSet());
        List<Video> videoList = videoService.getVideoListByVideoIds(videoIds);//批量查询视频列表
        for (VideoCollection videoCollection : videoCollectionList) {
            //当videoCollection的VideoId和video的id相同，则进行设置
            for (Video video : videoList) {
                if (videoCollection.getVideoId().equals(video.getId())) {
                    videoCollection.setVideoInfo(video);
                }
            }
        }
        return new PageResult<>(page.getTotal(), videoCollectionList);
    }


}
