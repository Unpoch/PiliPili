package com.wz.pilipili.media.service.impl;

import com.wz.pilipili.entity.media.MediaProcessHistory;
import com.wz.pilipili.media.mapper.MediaProcessHistoryMapper;
import com.wz.pilipili.media.service.MediaProcessHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-10
 */
@Service
public class MediaProcessHistoryServiceImpl extends ServiceImpl<MediaProcessHistoryMapper, MediaProcessHistory> implements MediaProcessHistoryService {

    /**
     * 添加到数据库
     */
    @Override
    public void addMediaProcessHistory(MediaProcessHistory mediaProcessHistory) {
        this.save(mediaProcessHistory);
    }
}
