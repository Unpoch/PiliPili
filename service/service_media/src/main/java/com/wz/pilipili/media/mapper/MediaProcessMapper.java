package com.wz.pilipili.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wz.pilipili.entity.media.MediaProcess;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-10
 */
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {


    /**
     * 项目选用的xxl-job的任务调度策略为分片广播，每一个执行器需要执行的任务
     * 需要去t_media_process中查询。
     * 需要提供的参数：分片参数shardIndex，分片总数shardTotal，执行的任务数量count，失败次数failCount
     */
    List<MediaProcess> selectListByShardIndex(
            @Param("shardIndex") int shardIndex
            , @Param("shardTotal") int shardTotal,
            @Param("count") int count,
            @Param("failCount") int failCount);

    /**
     * 开启一个任务
     * 采用乐观锁机制，谁执行该SQL语句成功，谁就抢到锁
     */
    int startTask(@Param("id") long id, @Param("failCount") int failCount);

}
