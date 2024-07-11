package com.wz.pilipili.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.media.MediaProcessHistory;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-10
 */
public interface MediaProcessHistoryService extends IService<MediaProcessHistory> {

    void addMediaProcessHistory(MediaProcessHistory mediaProcessHistory);
}
