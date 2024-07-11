package com.wz.pilipili.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.video.VideoCoin;

import java.util.Date;

/**
 * <p>
 * 视频硬币表 服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-17
 */
public interface VideoCoinService extends IService<VideoCoin> {

    VideoCoin getVideoCoinByUserIdAndVideoId(Long userId, Long videoId);

    void addVideoCoin(VideoCoin videoCoin);

    void updateVideoCoin(VideoCoin videoCoin);

    Long getVideoCoinsByVideoId(Long videoId);

    PageResult<VideoCoin> pageListVideoCoins(Integer pageNo, Integer pageSize, Long userId, String startTime,String endTime);
}
