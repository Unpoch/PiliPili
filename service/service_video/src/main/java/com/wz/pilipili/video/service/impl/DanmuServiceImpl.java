package com.wz.pilipili.video.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.wz.pilipili.constant.RedisConstant;
import com.wz.pilipili.entity.video.Danmu;
import com.wz.pilipili.video.mapper.DanmuMapper;
import com.wz.pilipili.video.service.DanmuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wz.pilipili.vo.video.VideoDanmuCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 弹幕记录表 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-22
 */
@Service
public class DanmuServiceImpl extends ServiceImpl<DanmuMapper, Danmu> implements DanmuService {

    @Autowired
    private DanmuMapper danmuMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 异步添加弹幕到数据库中
     */
    @Async      //异步调用
    @Override
    public void asyncAddDanmu(Danmu danmu) {
        baseMapper.insert(danmu);
    }

    /**
     * TODO ：解决缓存穿透问题
     * 获取视频弹幕
     * 查询策略：
     * 优先查询redis的弹幕数据
     * 如果没有查到，就去数据库中查，然后写入redis中
     */
    @Override
    public List<Danmu> getDanmus(Long videoId, String startTime, String endTime) throws Exception {
        //1.先去redis中查询
        String key = RedisConstant.VIDEO_DANMU + videoId;
        String value = redisTemplate.opsForValue().get(key);
        List<Danmu> danmuList;
        if (!StringUtils.isNullOrEmpty(value)) {
            danmuList = JSONObject.parseArray(value, Danmu.class);
            //根据时间进行筛选
            if (!StringUtils.isNullOrEmpty(startTime) &&
                    !StringUtils.isNullOrEmpty(endTime)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startDate = sdf.parse(startTime);
                Date endDate = sdf.parse(endTime);
                List<Danmu> filterDanmuList = new ArrayList<>();
                for (Danmu danmu : danmuList) {
                    Date createTime = danmu.getCreateTime();
                    if (createTime.after(startDate) && createTime.before(endDate)) {
                        filterDanmuList.add(danmu);
                    }
                }
                danmuList = filterDanmuList;
            }
        } else {//2.redis中没有，去数据库中查询，查询后放入redis中
            danmuList = baseMapper.selectList(new LambdaQueryWrapper<Danmu>()
                    .eq(Danmu::getVideoId, videoId)
                    .ge(Danmu::getCreateTime, startTime)
                    .le(Danmu::getCreateTime, endTime));
            //保存到redis中
            redisTemplate.opsForValue().set(key, JSONObject.toJSONString(danmuList));
        }
        return danmuList;
    }

    /**
     * 将Danmu添加到Redis中
     */
    @Override
    public void addDanmuToRedis(Danmu danmu) {
        //1.取出videoId对应的所有弹幕列表
        String key = RedisConstant.VIDEO_DANMU + danmu.getVideoId();
        String value = redisTemplate.opsForValue().get(key);
        List<Danmu> list = new ArrayList<>();
        if (!StringUtils.isNullOrEmpty(value)) {
            list = JSONObject.parseArray(value, Danmu.class);
        }
        //2.将该弹幕加入弹幕列表，然后放回redis中
        list.add(danmu);
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(list));
    }

    /**
     * 根据视频id集合 获取视频弹幕量
     */
    @Override
    public List<VideoDanmuCount> getVideoDanmuCountByVideoIds(Set<Long> videoIds) {
        return danmuMapper.getVideoDanmuCountByVideoIds(videoIds);
    }
}
