package com.wz.pilipili.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.video.Danmu;
import com.wz.pilipili.vo.video.VideoDanmuCount;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 弹幕记录表 服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-22
 */
public interface DanmuService extends IService<Danmu> {

    void asyncAddDanmu(Danmu danmu);

    List<Danmu> getDanmus(Long videoId,String startTime,String endTime) throws Exception;

    void addDanmuToRedis(Danmu danmu);

    List<VideoDanmuCount> getVideoDanmuCountByVideoIds(Set<Long> videoIds);
}
