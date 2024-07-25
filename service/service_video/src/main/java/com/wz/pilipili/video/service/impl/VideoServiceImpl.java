package com.wz.pilipili.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.wz.pilipili.constant.UserConstant;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.user.UserInfo;
import com.wz.pilipili.entity.video.*;
import com.wz.pilipili.exception.ConditionException;
import com.wz.pilipili.media.client.MediaFileFeignClient;
import com.wz.pilipili.user.client.UserCoinFeignClient;
import com.wz.pilipili.user.client.UserInfoFeignClient;
import com.wz.pilipili.util.HttpUtil;
import com.wz.pilipili.video.mapper.VideoLikeMapper;
import com.wz.pilipili.video.mapper.VideoMapper;
import com.wz.pilipili.video.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wz.pilipili.vo.media.FileInfo;
import com.wz.pilipili.vo.user.UserVideoHistory;
import com.wz.pilipili.vo.video.VideoDanmuCount;
import com.wz.pilipili.vo.video.VideoViewCount;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 视频投稿记录表 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-15
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Autowired
    private VideoTagService videoTagService;

    @Autowired
    private VideoCoinService videoCoinService;

    @Autowired
    private VideoViewService videoViewService;

    @Autowired
    private VideoLikeService videoLikeService;

    @Autowired
    private VideoShareService videoShareService;

    @Autowired
    private DanmuService danmuService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private MediaFileFeignClient mediaFileFeignClient;

    @Autowired
    private UserCoinFeignClient userCoinFeignClient;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    /**
     * 视频投稿
     */
    @Transactional
    @Override
    public void postVideo(Video video) {
        //1.往数据库中插入视频记录
        this.save(video);
        Long videoId = video.getId();
        List<VideoTag> videoTagList = video.getTagList();
        //2.获取视频的（视频标签VideoTag）列表，为每个VideoTag设置视频id
        videoTagList.forEach(item -> {
            item.setVideoId(videoId);
        });
        //3.批量添加视频标签到数据库
        videoTagService.batchInsertVideoTags(videoTagList);
    }

    /**
     * 分页查询视频
     */
    @Override
    public PageResult<Video> pageListVideos(Integer size, Integer no, String area) {
        //1.计算分页参数,创建Page对象
        int start = (no - 1) * size;
        int limit = size;
        Page<Video> pageParams = new Page<>(start, limit);
        //2.调用查询方法，分页查询，获取分页数据
        IPage<Video> page = baseMapper.selectPage(pageParams, new LambdaQueryWrapper<Video>()
                .eq(Video::getArea, area).orderByDesc(Video::getId));
        List<Video> videoList = page.getRecords();
        long total = page.getTotal();
        //3.统计播放量和弹幕量
        this.sumVideoPlaysAndDanmus(videoList);
        //4.封装tagList
        this.setTagListForVideoList(videoList);
        //5.封装PageResult对象进行返回
        return new PageResult<>(total, videoList);
    }

    /**
     * 统计视频播放量和弹幕量
     */
    private void sumVideoPlaysAndDanmus(List<Video> records) {
        //1.获取视频id集合
        Set<Long> videoIds = records.stream().map(Video::getId).collect(Collectors.toSet());
        //2.统计播放量
        Map<Long, Integer> viewCountMap = this.batchGetVideoView(videoIds);
        //3.统计弹幕量
        Map<Long, Integer> danmuCountMap = this.batchGetVideoDanmu(videoIds);
        //4.构建video的播放量和弹幕量数据
        records.forEach(video -> {
            video.setViewCount(viewCountMap.get(video.getId()));
            video.setDanmuCount(danmuCountMap.get(video.getId()));
        });
    }

    private Map<Long, Integer> batchGetVideoView(Set<Long> videoIds) {
        List<VideoViewCount> viewCountList = videoViewService.getVideoViewCountByVideoIds(videoIds);
        return viewCountList.stream().collect(
                Collectors.toMap(VideoViewCount::getVideoId, VideoViewCount::getCount));
    }

    private Map<Long, Integer> batchGetVideoDanmu(Set<Long> videoIds) {
        List<VideoDanmuCount> videoDanmuCountList = danmuService.getVideoDanmuCountByVideoIds(videoIds);
        return videoDanmuCountList.stream().collect(
                Collectors.toMap(VideoDanmuCount::getVideoId, VideoDanmuCount::getCount));
    }

    /**
     * 播放视频
     */
    @Override
    public void playVideo(HttpServletRequest request, HttpServletResponse response, Integer fileId) throws Exception {
        //1.根据文件id查询文件信息（远程调用service-media）
        FileInfo fileInfo = mediaFileFeignClient.getFileInfoByFileId(fileId);
        int totalSize = fileInfo.getSize();
        String url = fileInfo.getUrl();
        //2.获取请求头参数，将header放到map存储起来中
        HashMap<String, Object> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            headers.put(header, request.getHeader(header));
        }
        //3.获取Range请求头的参数，进行解析处理
        String rangeStr = request.getHeader("Range");//bytes=4554752-41670075
        String[] range;
        if (StringUtils.isNullOrEmpty(rangeStr)) {//如果为null，设置字节的范围为整个文件的大小
            rangeStr = "byte=0-" + (totalSize - 1);
        }
        range = rangeStr.split("bytes=|-");//分离之后，range[0]是空数据,range[1]是起始,range[2]是结束
        long begin = 0;//起始位置
        if (range.length >= 2) {//说明只有开始没有结束
            begin = Long.parseLong(range[1]);
        }
        long end = totalSize - 1;//结束 默认为totalSize - 1
        if (range.length >= 3) {//有结束位置
            end = Long.parseLong(range[2]);
        }
        long sliceLen = (end - begin) + 1; //实际分片长度
        //4.设置响应头Content-Range字段（Content-Range : bytes 4554752-41670075/41670076
        //告诉前端这次请求返回给前端的是什么范围的数据   bytes start-end/totalSize
        String contentRange = "bytes " + begin + "-" + end + "/" + totalSize;
        //5.设置响应头其他字段
        response.setHeader("Content-Range", contentRange);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Type", "video/mp4");
        response.setContentLength((int) sliceLen);
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        //6.使用HTTP请求 文件url
        HttpUtil.get(url, headers, response);
    }

    /**
     * 根据videoId获取Video
     */
    @Override
    public Video getVideoByVideoId(Long videoId) {
        return this.getById(videoId);
    }

    /**
     * 根据视频id集合查询视频列表
     */
    @Override
    public List<Video> getVideoListByVideoIds(Set<Long> videoIds) {
        return baseMapper.selectBatchIds(videoIds);
    }

    /**
     * 添加标签
     */
    @Override
    public Long addTag(Tag tag) {
        return videoTagService.addTag(tag);
    }

    /**
     * 删除视频标签
     */
    @Override
    public void deleteVideoTags(List<Long> tagIdList, Long videoId) {
        videoTagService.deleteVideoTags(tagIdList, videoId);
    }

    /**
     * 获取视频关联标签
     */
    @Override
    public List<Tag> getVideoTags(Long videoId) {
        return videoTagService.getTagListByVideoId(videoId);
    }

    /**
     * 视频投币
     */
    @Transactional
    @Override
    public void addVideoCoins(Long userId, VideoCoin videoCoin) {
        //1.参数校验
        Long videoId = videoCoin.getVideoId();
        Integer postCoins = videoCoin.getAmount();
        if (videoId == null) {
            throw new ConditionException("参数异常");
        }
        Video video = baseMapper.selectById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！");
        }
        //2.查询当前用户是否有足够的硬币，不够要抛出异常（远程调用）
        Integer userCoinAmount = userCoinFeignClient.getUserCoins(userId);
        userCoinAmount = userCoinAmount == null ? 0 : userCoinAmount;
        if (userCoinAmount < postCoins) {
            throw new ConditionException("硬币数量不足！");
        }
        //3.查询用户之前给视频投的硬币数
        //3.1 如果 == 0，表明还没有投过币，那就是插入一条投币记录
        //3.2 如果 != 0，表明已经投过币，那么就是将原投币记录的amount字段新增此次投币数量
        // （这里我们不对用户对视频的投币上限做控制）
        VideoCoin dbVideoCoin = videoCoinService.getVideoCoinByUserIdAndVideoId(userId, videoId);
        if (dbVideoCoin == null) {
            videoCoin.setUserId(userId);
            videoCoinService.addVideoCoin(videoCoin);
        } else {
            Integer newCoinAmount = dbVideoCoin.getAmount() + postCoins;
            videoCoin.setUserId(userId);
            videoCoin.setAmount(newCoinAmount);
            videoCoinService.updateVideoCoin(videoCoin);
        }
        //4.用户投完币后，需要更新用户的硬币总数（远程调用）
        //  还需要更新视频发布者的硬币个数
        Long upUserId = video.getUserId();//发布者的userId
        Integer upCoinAmount = userCoinFeignClient.getUserCoins(upUserId);
        userCoinFeignClient.updateUserCoin(userId, (userCoinAmount - postCoins));
        userCoinFeignClient.updateUserCoin(upUserId, (upCoinAmount + postCoins));
        //5. 增加投币用户的经验值(10经验/个)，增加被投币用户的经验值(1经验值/1个)
        userInfoFeignClient.increaseExperience(userId, UserConstant.TEN_EXPERIENCE * postCoins);
        userInfoFeignClient.increaseExperience(upUserId, UserConstant.ONE_EXPERIENCE * postCoins);
    }

    /**
     * 获取视频硬币数量
     */
    @Override
    public Map<String, Object> getVideoCoins(Long userId, Long videoId) {
        Long videoCoinsAmount = videoCoinService.getVideoCoinsByVideoId(videoId);
        //还要看用户是否投过币
        VideoCoin videoCoin = videoCoinService.getVideoCoinByUserIdAndVideoId(userId, videoId);
        boolean posted = videoCoin == null;
        Map<String, Object> result = new HashMap<>();
        result.put("count", videoCoinsAmount);
        result.put("posted", posted);
        return result;
    }

    /**
     * 获取视频详情
     */
    @Override
    public Map<String, Object> getVideoDetails(Long videoId) {
        //1.根据videoId查询视频信息
        Video video = baseMapper.selectById(videoId);
        List<VideoTag> tagList = videoTagService.getVideoTagsByVideoId(videoId);
        video.setTagList(tagList);
        //TODO : 根据用户喜好推荐视频
        //2.根据视频信息获取userId，根据userId查询用户信息（视频右上角可以查看发布视频的用户）
        Long userId = video.getUserId();
        UserInfo userInfo = userInfoFeignClient.getInfoById(userId);
        //3.封装Map返回
        Map<String, Object> result = new HashMap<>();
        result.put("video", video);
        result.put("userInfo", userInfo);
        return result;
    }

    /**
     * 根据userId查询用户发布的所有视频
     */
    @Override
    public List<Video> getVideoListByUserId(Long userId) {
        return baseMapper.selectList(new LambdaQueryWrapper<Video>()
                .eq(Video::getUserId, userId));
    }

    /**
     * 分页查询用户投稿视频
     */
    @Override
    public PageResult<Video> pageListUserVideos(Integer pageNo, Integer pageSize, Long userId) {
        int start = (pageNo - 1) * pageSize;
        Page<Video> pageParams = new Page<>(start, pageSize);
        IPage<Video> page = baseMapper.selectPage(pageParams, new LambdaQueryWrapper<Video>()
                .eq(Video::getUserId, userId));
        List<Video> videoList = page.getRecords();
        //设置video的danmuCount和viewCount
        this.sumVideoPlaysAndDanmus(videoList);
        return new PageResult<>(page.getTotal(), videoList);
    }

    /**
     * 分页查询用户观看历史记录
     */
    @Override
    public PageResult<UserVideoHistory> pageListVideoViewHistory(Integer pageNo, Integer pageSize, Long userId) {
        //1.分页查询VideoView
        PageResult<VideoView> videoViewPageResult = videoViewService.pageListVideoViews(pageNo, pageSize, userId);
        List<VideoView> videoViewList = videoViewPageResult.getList();
        Map<Long, VideoView> videoViewMap = videoViewList.stream().collect(Collectors.toMap(VideoView::getVideoId, item -> item));
        Long total = videoViewPageResult.getTotal();
        //2.筛选视频id，然后根据视频id批量查询视频
        Set<Long> videoIds = videoViewList.stream().map(VideoView::getVideoId).collect(Collectors.toSet());
        List<Video> videoList = this.getVideoListByVideoIds(videoIds);
        //3.videoId -> userId -> nick
        Set<Long> userIds = videoList.stream().map(Video::getUserId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = userInfoFeignClient.batchGetUserInfoListByUserIds(userIds);
        Map<Long, String> userNickMap = userInfoList.stream().collect(Collectors.toMap(UserInfo::getUserId, UserInfo::getNick));
        //4.封装UserVideoHistory返回
        List<UserVideoHistory> userVideoHistoryList = new ArrayList<>();
        videoList.forEach(video -> {
            UserVideoHistory userVideoHistory = new UserVideoHistory();
            BeanUtils.copyProperties(video, userVideoHistory);
            userVideoHistory.setVideoId(video.getId());
            userVideoHistory.setNick(userNickMap.get(video.getUserId()));//设置发布视频人的名字
            userVideoHistory.setCreateTime(videoViewMap.get(video.getId()).getCreateTime());//用户观看记录的时间
            userVideoHistoryList.add(userVideoHistory);
        });
        //5.封装PageResult对象返回
        return new PageResult<>(total, userVideoHistoryList);
    }

    /**
     * 分页查询用户最近点赞视频记录
     */
    @Override
    public PageResult<UserVideoHistory> pageListVideoLikeHistory(Integer pageNo, Integer pageSize, Long userId, String startTime, String endTime) {
        //1.分页查询VideoLike
        PageResult<VideoLike> videoLikePageResult = videoLikeService.pageListVideoLikes(pageNo, pageSize, userId, startTime, endTime);
        Long total = videoLikePageResult.getTotal();
        List<VideoLike> videoLikeList = videoLikePageResult.getList();
        Map<Long, VideoLike> videoLikeMap = videoLikeList.stream().collect(Collectors.toMap(VideoLike::getVideoId, item -> item));
        //2.筛选视频id，根据视频id查询视频信息
        Set<Long> videoIds = videoLikeList.stream().map(VideoLike::getVideoId).collect(Collectors.toSet());
        List<Video> videoList = this.getVideoListByVideoIds(videoIds);
        //3.videoId -> userId -> nick
        Set<Long> userIds = videoList.stream().map(Video::getUserId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = userInfoFeignClient.batchGetUserInfoListByUserIds(userIds);
        Map<Long, String> userNickMap = userInfoList.stream().collect(Collectors.toMap(UserInfo::getUserId, UserInfo::getNick));
        //4.封装UserVideoHistory返回
        List<UserVideoHistory> userVideoHistoryList = new ArrayList<>();
        videoList.forEach(video -> {
            UserVideoHistory userVideoHistory = new UserVideoHistory();
            BeanUtils.copyProperties(video, userVideoHistory);
            userVideoHistory.setVideoId(video.getId());
            userVideoHistory.setNick(userNickMap.get(video.getUserId()));//设置发布视频人的名字
            userVideoHistory.setCreateTime(videoLikeMap.get(video.getId()).getCreateTime());//用户点赞视频的时间
            userVideoHistoryList.add(userVideoHistory);
        });
        //5.封装PageResult对象返回
        return new PageResult<>(total, userVideoHistoryList);
    }

    /**
     * 分页查询用户最近投币视频记录
     */
    @Override
    public PageResult<UserVideoHistory> pageListVideoCoinHistory(Integer pageNo, Integer pageSize, Long userId, String startTime, String endTime) {
        //1.分页查询VideoCoin
        PageResult<VideoCoin> videoCoinPageResult = videoCoinService.pageListVideoCoins(pageNo, pageSize, userId, startTime, endTime);
        Long total = videoCoinPageResult.getTotal();
        List<VideoCoin> videoCoinList = videoCoinPageResult.getList();
        Map<Long, VideoCoin> videoCoinMap = videoCoinList.stream().collect(Collectors.toMap(VideoCoin::getVideoId, item -> item));
        //2.筛选视频id，根据视频id查询视频信息
        Set<Long> videoIds = videoCoinList.stream().map(VideoCoin::getVideoId).collect(Collectors.toSet());
        List<Video> videoList = this.getVideoListByVideoIds(videoIds);
        //3.videoId -> userId -> nick
        Set<Long> userIds = videoList.stream().map(Video::getUserId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = userInfoFeignClient.batchGetUserInfoListByUserIds(userIds);
        Map<Long, String> userNickMap = userInfoList.stream().collect(Collectors.toMap(UserInfo::getUserId, UserInfo::getNick));
        //4.封装UserVideoHistory返回
        List<UserVideoHistory> userVideoHistoryList = new ArrayList<>();
        videoList.forEach(video -> {
            UserVideoHistory userVideoHistory = new UserVideoHistory();
            BeanUtils.copyProperties(video, userVideoHistory);
            userVideoHistory.setVideoId(video.getId());
            userVideoHistory.setNick(userNickMap.get(video.getUserId()));//设置发布视频人的名字
            userVideoHistory.setCreateTime(videoCoinMap.get(video.getId()).getUpdateTime());//用户投币视频的时间
            userVideoHistoryList.add(userVideoHistory);
        });
        //5.封装PageResult对象返回
        return new PageResult<>(total, userVideoHistoryList);
    }

    /**
     * 根据userId获取用户投稿视频点赞总数
     */
    @Override
    public Integer getUserVideoLikes(Long userId) {
        //1.根据userId查询用户发布的所有视频
        List<Video> videoList = this.getVideoListByUserId(userId);
        //2.根据视频id 查询点赞表，统计点赞数
        Set<Long> videoIds = videoList.stream().map(Video::getId).collect(Collectors.toSet());
        return videoLikeService.getVideoLikesByVideoIds(videoIds);
    }

    /**
     * 分页查询某个分区下所有视频
     */
    @Override
    public PageResult<Video> pageListAreaVideo(Integer pageNo, Integer pageSize, String area) {
        //1.分页查询
        int start = (pageNo - 1) * pageSize;
        Page<Video> pageParams = new Page<>(start, pageSize);
        IPage<Video> page = baseMapper.selectPage(pageParams, new LambdaQueryWrapper<Video>()
                .eq(Video::getArea, area)
                .orderByDesc(Video::getId));
        List<Video> videoList = page.getRecords();
        //2.封装Video的属性,统计弹幕量和播放量
        this.sumVideoPlaysAndDanmus(videoList);
        //3.封装videoTagList
        this.setTagListForVideoList(videoList);
        //4.封装PageResult返回
        return new PageResult<>(page.getTotal(), videoList);
    }

    /**
     * 查询所有分区
     */
    @Override
    public List<Area> getAllAreas() {
        return areaService.getAllAreas();
    }

    /**
     * 视频分享
     * TODO:链接应该采用短链 而不是长链接
     */
    @Override
    public String shareVideo(Long userId, Long videoId) {
        //1.查询视频信息
        Video video = this.getVideoByVideoId(videoId);
        String title = video.getTitle();//视频标题
        String url = video.getUrl();
        //2.插入视频分享表
        VideoShare videoShare = new VideoShare();
        videoShare.setUserId(userId);
        videoShare.setVideoId(videoId);
        videoShareService.addVideoShare(videoShare);
        //3.将生成链接返回
        return "【" + title + "】" + url;
    }

    /**
     * 查询视频分享数
     */
    @Override
    public Map<String, Object> getVideoShareCount(Long userId, Long videoId) {
        //获取视频点赞数
        Integer count = videoShareService.getVideoShareCount(videoId);
        //看userId是否已经分享过
        VideoShare videoShare = videoShareService.getVideoShareByVideoIdAndUserId(userId, videoId);
        boolean share = videoShare == null;
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("share", share);
        return result;
    }

    private void setTagListForVideoList(List<Video> videoList) {
        //1.筛选videoId集合，查询VideoTag集合
        Set<Long> videoIds = videoList.stream().map(Video::getId).collect(Collectors.toSet());
        List<VideoTag> videoTagList = videoTagService.getVideoTagListByVideoIds(videoIds);
        //2.videoId -> videoTagList
        Map<Long, List<VideoTag>> videoTagListMap = videoTagList.stream().collect(Collectors.groupingBy(VideoTag::getVideoId));
        videoList.forEach(video -> {
            video.setTagList(videoTagListMap.get(video.getId()));
        });
    }
}
