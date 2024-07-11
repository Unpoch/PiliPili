package com.wz.pilipili.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.media.MediaFile;
import com.wz.pilipili.entity.media.MediaProcess;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-10
 */
public interface MediaProcessService extends IService<MediaProcess> {

    void addWaitingTask(MediaFile mediaFile, String mimeType);

    List<MediaProcess> selectListByShardIndex(int shardIndex, int shardTotal, int count, int failCount);

    boolean startTask(long id, int failCount);

    void saveProcessFinishStatus(Long taskId, String status, String md5,String newObjectName, String url, String errorMsg);
}
