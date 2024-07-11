package com.wz.pilipili.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.video.VideoCoin;
import com.wz.pilipili.video.mapper.VideoCoinMapper;
import com.wz.pilipili.video.service.VideoCoinService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 视频硬币表 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-17
 */
@Service
public class VideoCoinServiceImpl extends ServiceImpl<VideoCoinMapper, VideoCoin> implements VideoCoinService {

    @Autowired
    private VideoCoinMapper videoCoinMapper;

    /**
     * 根据userId和videoId获取视频硬币记录
     */
    @Override
    public VideoCoin getVideoCoinByUserIdAndVideoId(Long userId, Long videoId) {
        return baseMapper.selectOne(new LambdaQueryWrapper<VideoCoin>()
                .eq(VideoCoin::getUserId, userId)
                .eq(VideoCoin::getVideoId, videoId));
    }

    /**
     * 添加视频硬币记录（投币记录）
     */
    @Override
    public void addVideoCoin(VideoCoin videoCoin) {
        baseMapper.insert(videoCoin);
    }

    /**
     * 更新视频硬币记录表（用户投币数）
     */
    @Override
    public void updateVideoCoin(VideoCoin videoCoin) {
        Long videoId = videoCoin.getVideoId();
        Long userId = videoCoin.getUserId();
        baseMapper.update(videoCoin, new LambdaQueryWrapper<VideoCoin>()
                .eq(VideoCoin::getUserId, userId)
                .eq(VideoCoin::getVideoId, videoId));
    }

    /**
     * 根据videoId查询视频硬币数量
     */
    @Override
    public Long getVideoCoinsByVideoId(Long videoId) {
        return videoCoinMapper.getVideoCoinsByVideoId(videoId);
    }

    /**
     * 分页查询最近视频投币记录
     */
    @Override
    public PageResult<VideoCoin> pageListVideoCoins(Integer pageNo, Integer pageSize, Long userId, String startTime, String endTime) {
        int start = (pageNo - 1) * pageSize;
        Page<VideoCoin> pageParams = new Page<>(start, pageSize);
        IPage<VideoCoin> page = baseMapper.selectPage(pageParams, new LambdaQueryWrapper<VideoCoin>()
                .eq(VideoCoin::getUserId, userId)
                .ge(VideoCoin::getUpdateTime, startTime)
                .le(VideoCoin::getUpdateTime, endTime)
                .orderByDesc(VideoCoin::getUpdateTime));//投币时间越晚
        return new PageResult<>(page.getTotal(), page.getRecords());
    }
}
