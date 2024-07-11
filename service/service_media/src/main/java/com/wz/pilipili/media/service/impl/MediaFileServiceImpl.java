package com.wz.pilipili.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.wz.pilipili.constant.RedisConstant;
import com.wz.pilipili.entity.media.MediaFile;
import com.wz.pilipili.exception.ConditionException;
import com.wz.pilipili.media.mapper.MediaFileMapper;
import com.wz.pilipili.media.service.MediaFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wz.pilipili.media.service.MediaProcessService;
import com.wz.pilipili.media.util.MinioUtil;
import com.wz.pilipili.util.UUIDGenerator;
import com.wz.pilipili.vo.media.FileInfo;
import io.minio.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 文件表 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-10
 */
@Service
public class MediaFileServiceImpl extends ServiceImpl<MediaFileMapper, MediaFile> implements MediaFileService {

    @Autowired
    private MinioClient minioClient;

    //注入代理对象，为了进行事务控制
    @Autowired
    private MediaFileService currentProxy;

    @Autowired
    private MediaProcessService mediaProcessService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${minio.endpoint}")
    private String minioServer;

    //存储普通文件
    @Value("${minio.bucket.files}")
    private String bucketCommonFiles;

    //存储视频文件
    @Value("${minio.bucket.videofiles}")
    private String bucketVideoFiles;

    public static final int BUFFER_SIZE = 1024 * 1024 * 10;//缓冲区大小10MB

    /**
     * 上传普通文件
     * 如果传入objectName要按objectName的目录去存储，如果不传就按年月日目录结构去存储
     */
    @Transactional
    @Override
    public String uploadFile(MultipartFile file, String objectName) throws Exception {
        //1.根据文件的扩展名获取文件的mimeType
        String extension = MinioUtil.getFileType(file);
        String mimeType = MinioUtil.getMimeType(extension);
        //2.获取文件的存储在Minio中的目录
        //获取默认存储目录,如果objectName没有传入就按照默认存储目录存储
        String defaultFolderPath = MinioUtil.getDefaultFolderPath();
        String fileName = file.getOriginalFilename();
        if (StringUtils.isNullOrEmpty(objectName)) {
            objectName = defaultFolderPath + fileName;
        }
        //3.上传文件到Minio
        String fileUrl = this.addMediaFileToMinIO(file.getInputStream(), extension, bucketCommonFiles, objectName);
        //4.获取文件的md5，通过md5去数据库查询该文件是否存在，如果存在则不上传
        String fileMD5 = this.getFileMD5(file);
        MediaFile mediaFile = currentProxy.addMediaFileToDB(fileMD5, fileName, objectName, bucketCommonFiles, mimeType, fileUrl);
        if (mediaFile == null) {
            throw new ConditionException("文件上传后保存信息失败！");
        }
        //5.将文件路径返回
        return fileUrl;
    }

    /**
     * 将文件保存到t_media_file
     * 若是视频文件且不是.mp4格式的文件，要记录任务，后续要对文件进行转码处理
     */
    @Transactional
    @Override
    public MediaFile addMediaFileToDB(String fileMD5, String fileName, String objectName, String bucket, String mimeType, String fileUrl) {
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileName(fileName);//如果视频需要转码，那么fileName后续会发生变化
        mediaFile.setUrl(fileUrl);//如果视频需要转码，那么fileUrl后续会发生变化
        // mediaFile.setType(mimeType);
        mediaFile.setObjectName(objectName);//如果视频需要转码，那么objectName后续会发生变化
        mediaFile.setBucket(bucket);
        mediaFile.setMd5(fileMD5);
        boolean save = this.save(mediaFile);
        if (!save) {
            return null;
        }
        //添加待处理任务
        mediaProcessService.addWaitingTask(mediaFile, mimeType);
        return mediaFile;
    }

    // TODO：下载文件的方法


    /**
     * 分块上传文件
     * 肯定是视频文件
     */
    @Override
    public String uploadChunk(MultipartFile file, String fileMD5, Integer chunkNo, Integer totalChunks) throws Exception {
        if (file == null || chunkNo == null || totalChunks == null) {
            throw new ConditionException("参数异常！");
        }
        //1.获取文件相关信息
        String extension = MinioUtil.getFileType(file);
        String mimeType = MinioUtil.getMimeType(extension);
        String chunkObjectName = MinioUtil.getChunkFileFolderPath(fileMD5) + chunkNo;
        //2.设置文件上传redis相关key
        String uploadedSizeKey = RedisConstant.UPLOADED_SIZE_KEY + fileMD5;//已上传文件大小
        String uploadedNoKey = RedisConstant.UPLOADED_NO_KEY + fileMD5;//已上传的文件分块序号
        //3.获取当前已上传的文件大小 和 分块文件路径
        String uploadedSizeStr = redisTemplate.opsForValue().get(uploadedSizeKey);
        Long uploadedSize = (uploadedSizeStr == null) ? 0L : Long.parseLong(uploadedSizeStr);
        //4.根据分块序号不同，上传分块文件并更新对应key的value
        String chunkPath = "";
        if (chunkNo == 1) {
            chunkPath = this.addMediaFileToMinIO(file.getInputStream(), extension, bucketVideoFiles, chunkObjectName);
            if (StringUtils.isNullOrEmpty(chunkPath)) {
                throw new ConditionException("上传失败！");
            }
            redisTemplate.opsForValue().set(uploadedNoKey, "1");
        } else {
            chunkPath = this.addMediaFileToMinIO(file.getInputStream(), extension, bucketVideoFiles, chunkObjectName);
            if (StringUtils.isNullOrEmpty(chunkPath)) {
                throw new ConditionException("上传失败！");
            }
            redisTemplate.opsForValue().increment(uploadedNoKey);
        }
        //5.更新已上传分块文件的总大小
        uploadedSize += file.getSize();
        redisTemplate.opsForValue().set(uploadedSizeKey, String.valueOf(uploadedSize));
        //6.根据当前已上传文件的序号 判断是否是最后一块
        String uploadedNoStr = redisTemplate.opsForValue().get(uploadedNoKey);
        assert uploadedNoStr != null;
        Integer uploadedNo = Integer.valueOf(uploadedNoStr);
        String resultUrl = "";
        //7.如果是最后一块，则需要合并所有分块，并删除所有分块，且删除redis相关key的value，
        // 进行视频转码需求，最后插入数据库
        if (uploadedNo.equals(totalChunks)) {
            //获取所有key
            List<String> keyList = Arrays.asList(uploadedNoKey, uploadedSizeKey);
            redisTemplate.delete(keyList);//根据key删除所有value
            //为防止文件名碰撞,根据MD5值和UUID生成文件名，然后合并分块并得到objectName
            String fileName = UUIDGenerator.generateUUID(fileMD5) + "." + extension;
            String fileNameWithoutExtension = MinioUtil.getFileNameWithoutExtension(fileName);
            String objectName = composeChunks(fileMD5, fileName, totalChunks);
            resultUrl = MinioUtil.getUploadFileUrl(minioServer, bucketVideoFiles, objectName);
            if (StringUtils.isNullOrEmpty(resultUrl)) {
                throw new ConditionException("文件合并失败！！");
            }
            //上传完成后插入数据库
            currentProxy.addMediaFileToDB(fileMD5, fileNameWithoutExtension, objectName, bucketVideoFiles, mimeType, resultUrl);
        }
        return resultUrl;
    }

    /**
     * 合并分块，返回合并后文件的objectName
     */
    private String composeChunks(String fileMD5, String fileName, int totalChunks) throws Exception {
        List<ComposeSource> sources = new ArrayList<>();
        //这里还是存在一定的碰撞风险
        for (int i = 1; i <= totalChunks; i++) {
            String chunkObjectName = MinioUtil.getChunkFileFolderPath(fileMD5) + i;
            sources.add(ComposeSource.builder().
                    bucket(bucketVideoFiles).
                    object(chunkObjectName).
                    build());
        }
        String objectName = MinioUtil.getDefaultFolderPath() + fileName;
        minioClient.composeObject(
                ComposeObjectArgs.builder()
                        .bucket(bucketVideoFiles)
                        .object(objectName)
                        .sources(sources)
                        .build()
        );
        //删除所有分块文件
        for (int i = 1; i <= totalChunks; i++) {
            String chunkObjectName = MinioUtil.getChunkFileFolderPath(fileMD5) + i;
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketVideoFiles)
                            .object(chunkObjectName)
                            .build()
            );
        }
        return objectName;
    }


    /**
     * 将文件上传到Minio，并将文件地址返回
     */
    @Override
    public String addMediaFileToMinIO(InputStream fileIS, String extension, String bucket, String objectName) throws Exception {
        try {
            String mimeType = MinioUtil.getMimeType(extension);
            PutObjectArgs upObj = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(fileIS, -1, BUFFER_SIZE) //-1表示不知道流的长度，BUFFER_SIZE设置缓冲区大小
                    .contentType(mimeType)
                    .build();
            minioClient.putObject(upObj);
            return MinioUtil.getUploadFileUrl(minioServer, bucket, objectName);
        } catch (Exception e) {
            throw new ConditionException("上传文件到文件系统失败！");
        } finally {
            fileIS.close();
        }
    }

    /**
     * 从Minio上下载文件
     */
    @Override
    public File downloadFileForMinio(String bucket, String objectName) {
        //临时文件
        File minioFile = null;
        FileOutputStream outputStream = null;
        try {
            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
            //创建临时文件
            minioFile = File.createTempFile("minio", ".merge");
            outputStream = new FileOutputStream(minioFile);
            IOUtils.copy(stream, outputStream);
            return minioFile;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 从minio删除文件
     */
    @Override
    public void deleteFileFromMinio(String bucket, String objectName) {
        try {
            // 删除对象
            minioClient.removeObject(
                    io.minio.RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new ConditionException("删除文件失败！");
        }
    }


    /**
     * 获取文件的md5字符串
     */
    @Override
    public String getFileMD5(MultipartFile file) throws Exception {
        return MinioUtil.getFileMd5(file);
    }


    /**
     * 检查文件
     * 检查文件是否已经存在
     */
    @Override
    public String checkFile(String fileMD5) throws Exception {
        String url = "";
        MediaFile mediaFile = this.getOne(new LambdaQueryWrapper<MediaFile>().eq(MediaFile::getMd5, fileMD5));
        //1.先查询数据库
        if (mediaFile != null) {
            //2.如果数据库存在再查询Minio
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(mediaFile.getBucket())
                    .object(mediaFile.getObjectName())
                    .build();
            //远程查询得到文件流
            InputStream inputStream = minioClient.getObject(getObjectArgs);
            //文件已存在
            if (inputStream != null) {
                url = mediaFile.getUrl();
            }
        }
        return url;
    }


    /**
     * 检查分块文件是否已经上传
     */
    @Override
    public Boolean checkChunk(String fileMD5, Integer chunkNo) {
        String chunkObjectName = MinioUtil.getChunkFileFolderPath(fileMD5) + chunkNo;
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketVideoFiles)
                    .object(chunkObjectName)
                    .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据文件md5获取文件
     */
    @Override
    public MediaFile getFileByMD5(String md5) {
        return this.getOne(new LambdaQueryWrapper<MediaFile>()
                .eq(MediaFile::getMd5, md5));
    }

    /**
     * 更新数据表记录
     */
    @Override
    public void updateMediaFile(MediaFile mediaFile) {
        this.updateById(mediaFile);
    }


    /**
     * 根据文件id获取文件信息
     */
    @Override
    public FileInfo getFileInfoByFileId(Integer fileId) throws Exception {
        //1.数据库查询
        MediaFile mediaFile = this.getById(fileId);
        if (mediaFile == null) {
            return null;
        }
        //2.minio获取文件元数据
        StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(mediaFile.getBucket())
                        .object(mediaFile.getObjectName())
                        .build()
        );
        int size = (int) stat.size();
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileId(fileId);
        fileInfo.setSize(size);
        fileInfo.setUrl(mediaFile.getUrl());
        return fileInfo;
    }

}
