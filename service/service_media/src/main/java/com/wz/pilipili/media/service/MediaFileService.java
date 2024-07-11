package com.wz.pilipili.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.media.MediaFile;
import com.wz.pilipili.vo.media.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

/**
 * <p>
 * 文件表 服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-10
 */
public interface MediaFileService extends IService<MediaFile> {

    String uploadFile(MultipartFile file, String objectName) throws Exception;

    String getFileMD5(MultipartFile file) throws Exception;

    String checkFile(String fileMD5) throws Exception;

    MediaFile addMediaFileToDB(String fileMD5, String fileName, String objectName, String bucket, String mimeType, String fileUrl);

    String addMediaFileToMinIO(InputStream fileIS, String mimeType, String bucket, String objectName) throws Exception;

    String uploadChunk(MultipartFile file, String fileMD5, Integer chunkNo, Integer totalChunks) throws Exception;

    Boolean checkChunk(String fileMD5, Integer chunkNo);

    MediaFile getFileByMD5(String md5);

    void updateMediaFile(MediaFile mediaFile);

    File downloadFileForMinio(String bucket, String objectName);

    void deleteFileFromMinio(String bucket,String objectName);

    FileInfo getFileInfoByFileId(Integer fileId) throws Exception;
}
