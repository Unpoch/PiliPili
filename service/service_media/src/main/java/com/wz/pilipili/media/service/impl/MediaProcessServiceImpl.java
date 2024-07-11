package com.wz.pilipili.media.service.impl;

import com.wz.pilipili.entity.media.MediaFile;
import com.wz.pilipili.entity.media.MediaProcess;
import com.wz.pilipili.entity.media.MediaProcessHistory;
import com.wz.pilipili.media.mapper.MediaProcessMapper;
import com.wz.pilipili.media.service.MediaFileService;
import com.wz.pilipili.media.service.MediaProcessHistoryService;
import com.wz.pilipili.media.service.MediaProcessService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wz.pilipili.media.util.MinioUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-10
 */
@Service
public class MediaProcessServiceImpl extends ServiceImpl<MediaProcessMapper, MediaProcess> implements MediaProcessService {

    @Autowired
    private Set<String> videoMimeTypes;//需要转码的mimeType

    @Autowired
    private MediaProcessMapper mediaProcessMapper;

    @Autowired
    private MediaFileService mediaFileService;

    @Autowired
    private MediaProcessHistoryService mediaProcessHistoryService;

    /**
     * 根据当前文件 ，加入文件处理任务
     */
    @Override
    public void addWaitingTask(MediaFile mediaFile, String mimeType) {
        //根据mimeType，判断是否需要视频转码
        if (videoMimeTypes.contains(mimeType)) {
            MediaProcess mediaProcess = new MediaProcess();
            mediaProcess.setFileName(mediaFile.getFileName());
            mediaProcess.setMd5(mediaFile.getMd5());
            mediaProcess.setBucket(mediaFile.getBucket());
            mediaProcess.setObjectName(mediaFile.getObjectName());
            //状态是未处理的
            mediaProcess.setStatus("1");
            mediaProcess.setFailCount(0);//失败次数
            mediaProcess.setUrl(null);//还未定
            this.save(mediaProcess);
        }
    }

    /**
     * 执行器(shardIndex)查询其要执行的任务
     */
    @Override
    public List<MediaProcess> selectListByShardIndex(int shardIndex, int shardTotal, int count, int failCount) {
        return mediaProcessMapper.selectListByShardIndex(shardIndex, shardTotal, count, failCount);
    }

    /**
     * 乐观锁机制
     * 执行器开启一个任务：哪个执行器执行这条语句成功，哪个执行器执行任务（分布式锁）
     */
    @Override
    public boolean startTask(long id, int failCount) {
        return mediaProcessMapper.startTask(id, failCount) > 0;
    }

    /**
     * 任务处理成功后： 更新任务状态
     */
    @Transactional //涉及多个数据库的操作，加事务
    @Override
    public void saveProcessFinishStatus(Long taskId, String status, String md5,String newObjectName, String url, String errorMsg) {
        //1.判断要更新的任务是否存在
        MediaProcess dbMediaProcess = this.getById(taskId);
        if (dbMediaProcess == null) {
            return;
        }
        //2.如果任务执行失败，更新MediaProcess的状态
        if (status.equals("3")) {
            dbMediaProcess.setStatus("3");
            dbMediaProcess.setFailCount(dbMediaProcess.getFailCount() + 1);
            dbMediaProcess.setErrormsg(errorMsg);
            this.updateById(dbMediaProcess);
            return;
        }
        //3.如果任务执行成功
        //1)更新t_media_file中的url
        MediaFile mediaFile = mediaFileService.getFileByMD5(md5);
        mediaFile.setUrl(url);
        mediaFile.setObjectName(newObjectName);
        mediaFileService.updateMediaFile(mediaFile);
        //2)更新MediaProcess的状态
        dbMediaProcess.setStatus("2");
        dbMediaProcess.setFinishTime(new Date());
        dbMediaProcess.setObjectName(newObjectName);
        dbMediaProcess.setUrl(url);
        this.updateById(dbMediaProcess);
        //3)将MediaProcess记录插入到t_media_process_history(历史任务记录表)
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(dbMediaProcess, mediaProcessHistory);
        mediaProcessHistoryService.addMediaProcessHistory(mediaProcessHistory);
        //4)t_media_process删除当前任务记录
        this.removeById(taskId);
    }
}
