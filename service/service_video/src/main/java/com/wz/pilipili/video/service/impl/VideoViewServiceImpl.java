package com.wz.pilipili.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.video.VideoView;
import com.wz.pilipili.util.IpUtil;
import com.wz.pilipili.video.mapper.VideoViewMapper;
import com.wz.pilipili.video.service.VideoViewService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wz.pilipili.vo.video.VideoViewCount;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 视频观看记录表 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-17
 */
@Service
public class VideoViewServiceImpl extends ServiceImpl<VideoViewMapper, VideoView> implements VideoViewService {

    @Autowired
    private VideoViewMapper videoViewMapper;

    /**
     * 添加视频播放记录
     */
    @Override
    public void addVideoView(VideoView videoView, HttpServletRequest request) {
        //1.获取userId和videoId
        Long userId = videoView.getUserId();
        Long videoId = videoView.getVideoId();
        //2.生成clientId
        String agent = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(agent);
        String clientId = String.valueOf(userAgent.getId());
        //3.构建插入数据库的参数
        String ip = IpUtil.getIP(request);
        Map<String, Object> params = new HashMap<>();
        if (userId != null) {
            params.put("userId", userId);
        } else {//游客
            params.put("ip", ip);
            params.put("clientId", clientId);
        }
        Date now = new Date();
        SimpleDateFormat sft = new SimpleDateFormat("yyyy-MM-dd");
        params.put("today", sft.format(now));
        params.put("videoId", videoId);
        //4.添加观看记录。先查询是否存在对应的观看记录，不存在则插入（每人对每一视频每天产生的播放量为最多为1）
        VideoView dbVideoView = videoViewMapper.getVideoView(params);
        if (dbVideoView == null) {
            videoView.setIp(ip);//这里如果是登录用户，那么videoId和userId已经被设置过了
            videoView.setClientId(clientId);
            baseMapper.insert(videoView);
        }
    }

    /**
     * 查询视频播放量
     */
    @Override
    public Integer getViewCounts(Long videoId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<VideoView>()
                .eq(VideoView::getVideoId, videoId));
    }

    /**
     * 根据视频id集合查询 对应视频播放量
     */
    @Override
    public List<VideoViewCount> getVideoViewCountByVideoIds(Set<Long> videoIds) {
        return videoViewMapper.getVideoViewCountByVideoIds(videoIds);
    }

    /**
     * 分页查询视频观看记录
     */
    @Override
    public PageResult<VideoView> pageListVideoViews(Integer pageNo, Integer pageSize, Long userId) {
        int start = (pageNo - 1) * pageSize;
        Page<VideoView> pageParams = new Page<>(start, pageSize);
        IPage<VideoView> page = baseMapper.selectPage(pageParams,
                new LambdaQueryWrapper<VideoView>()
                        .eq(VideoView::getUserId, userId)
                        .orderByDesc(VideoView::getId));
        return new PageResult<>(page.getTotal(), page.getRecords());
    }

}
