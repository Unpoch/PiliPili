package com.wz.pilipili.media.jobhandler;

import com.wz.pilipili.entity.media.MediaProcess;
import com.wz.pilipili.exception.ConditionException;
import com.wz.pilipili.media.service.MediaFileService;
import com.wz.pilipili.media.service.MediaProcessService;
import com.wz.pilipili.media.util.MP4VideoUtil;
import com.wz.pilipili.media.util.MinioUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 视频转码任务类
 */
@Component
@Slf4j
public class VideoTask {

    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    @Autowired
    private MediaProcessService mediaProcessService;

    @Autowired
    private MediaFileService mediaFileService;

    /**
     * 分片广播任务
     * 任务失败要记录任务失败结果
     */
    @XxlJob("VideoJobHandler")
    public void shardingJobHandler() throws Exception {
        //1.获取分片参数
        int shardIndex = XxlJobHelper.getShardIndex();//执行器的序号，从0开始
        int shardTotal = XxlJobHelper.getShardTotal();//执行器的总数
        //2.获取cpu核心数，以便确定 执行器处理任务个数、创建线程池时参数
        //  处理任务最大的数量就是cpu的核心数
        int processors = Runtime.getRuntime().availableProcessors();
        //3.查询待处理任务
        List<MediaProcess> videoTaskList = mediaProcessService.selectListByShardIndex(shardIndex, shardTotal, 5, processors);
        //4.获取任务数量，然后创建线程池处理任务
        int taskCounts = videoTaskList.size();
        if (taskCounts == 0) {//没有任务
            return;
        }
        ExecutorService executorService = Executors.newFixedThreadPool(taskCounts);
        /*
        计数器：为了让这taskCounts个任务执行完成之后，才返回
        因为使用线程池创建了taskCounts个线程，这个方法就会立即返回了（execute中只是线程执行的逻辑）
        我们想要的效果是：taskCounts个线程执行其转码任务完成后，该方法才返回，这样才能进行下一次任务调度。因此用到了CountDownLatch
         */
        CountDownLatch countDownLatch = new CountDownLatch(taskCounts);
        //5.遍历待处理任务列表，将任务加入线程池
        videoTaskList.forEach(mediaProcess -> {
            //任务执行逻辑
            executorService.execute(() -> {
                try {
                    //1) 抢任务执行（开启任务执行）
                    Long taskId = mediaProcess.getId();
                    boolean b = mediaProcessService.startTask(taskId, 3);
                    if (!b) {
                        log.debug("抢占任务失败，任务id:{}", taskId);
                        return;
                    }
                    //2) 执行视频转码（从minio下载需要转码的视频，本地处理）
                    String bucket = mediaProcess.getBucket();
                    String oldObjectName = mediaProcess.getObjectName();
                    String fileName = mediaProcess.getFileName();
                    String md5 = mediaProcess.getMd5();
                    File file = mediaFileService.downloadFileForMinio(bucket, oldObjectName);//从Minio上下载视频
                    if (file == null) {
                        //保存任务处理失败的结果
                        mediaProcessService.saveProcessFinishStatus(taskId, "3", md5, null, null, "下载视频到本地失败");
                        throw new ConditionException("下载视频出错！！");
                    }
                    String inputFilePath = file.getAbsolutePath();//要转换的文件路径
                    String outputFileName = fileName + ".mp4";//转换后文件的名称
                    //转换后文件的路径：创建一个临时文件，作为转换后的文件
                    File tempFile;
                    try {
                        tempFile = File.createTempFile("minio", ".temp");
                    } catch (IOException e) {
                        mediaProcessService.saveProcessFinishStatus(taskId, "3", md5, null, null, "临时文件创建失败");
                        throw new ConditionException("临时文件创建失败！");
                    }

                    String outputFilePath = tempFile.getParent();//转换后文件的路径
                    String result = MP4VideoUtil.generateMp4(ffmpegPath, inputFilePath, outputFilePath, outputFileName);
                    if (!result.equals("success")) {
                        mediaProcessService.saveProcessFinishStatus(taskId, "3", md5, null, null, "视频转码失败");
                        throw new ConditionException("视频转码失败！");
                    }
                    //3) 处理好的视频上传到minio，（我认为要将原url视频的文件删除）
                    //4) 保存任务处理结果到数据库
                    String extension = ".mp4";
                    String url = null;
                    try {
                        String newObjectName = MinioUtil.getNewObjectName(oldObjectName, fileName, extension);
                        url = mediaFileService.addMediaFileToMinIO(Files.newInputStream(file.toPath()), extension, bucket, newObjectName);
                        //并将状态更新为成功
                        mediaProcessService.saveProcessFinishStatus(taskId, "2", md5, newObjectName, url, null);
                        //删除 oldObjectName 的视频（将转码前的视频删除）
                        mediaFileService.deleteFileFromMinio(bucket,oldObjectName);
                    } catch (Exception e) {
                        mediaProcessService.saveProcessFinishStatus(taskId, "3", md5, null, null, "处理后视频上传或入库失败");
                        throw new ConditionException("上传文件到Minio失败！");
                    }
                } finally {//计数器 - 1
                    countDownLatch.countDown();
                }
            });
        });
        //让线程阻塞，直到计数器 == 0
        // countDownLatch.await();
        //要设置最大等待时间，因为如果有某个线程没有执行完逻辑就退出了，那么计数器将无法到0，方法将无法返回
        //因此要设置兜底的策略：等待最多30min解除阻塞
        countDownLatch.await(30, TimeUnit.MINUTES);
    }

}
