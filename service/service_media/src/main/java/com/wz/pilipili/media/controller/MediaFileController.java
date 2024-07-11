package com.wz.pilipili.media.controller;


import com.mysql.cj.util.StringUtils;
import com.wz.pilipili.media.service.MediaFileService;
import com.wz.pilipili.media.util.MinioUtil;
import com.wz.pilipili.result.R;
import com.wz.pilipili.vo.media.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 文件表 前端控制器
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-10
 */
@RestController
@RequestMapping("/media/file")
public class MediaFileController {

    @Autowired
    private MediaFileService fileService;


    /**
     * 获取文件的md5
     */
    @PostMapping("/md5files")
    public R<String> getFileMD5(MultipartFile file) throws Exception {
        String fileMD5 = fileService.getFileMD5(file);
        return new R<>(fileMD5);
    }

    /**
     * 上传普通文件
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file, @RequestParam(value = "objectName", required = false) String objectName) throws Exception {
        String url = fileService.checkFile(MinioUtil.getFileMd5(file));
        if (StringUtils.isNullOrEmpty(url)) {
            url = fileService.uploadFile(file, objectName);
        }
        return new R<>(url);
    }


    /**
     * 分块上传文件
     */
    @PutMapping("/uploadChunk")
    public R<String> uploadChunk(MultipartFile file,
                                 String fileMD5,
                                 Integer chunkNo,
                                 Integer totalChunks) throws Exception {
        String fileUrl = fileService.checkFile(fileMD5);
        if (StringUtils.isNullOrEmpty(fileUrl)) {
            fileUrl = fileService.uploadChunk(file, fileMD5, chunkNo, totalChunks);
        }
        return new R<>(fileUrl);
    }

    /**
     * 检查分块文件是否已经上传
     */
    @GetMapping("/checkChunk")
    public R<Boolean> checkChunk(@RequestParam String fileMD5, @RequestParam Integer chunkNo) {
        return new R<>(fileService.checkChunk(fileMD5, chunkNo));
    }

    /*
    远程调用接口
    根据文件id查询文件信息
     */
    @GetMapping("/inner/getFileInfo")
    public FileInfo getFileInfoByFileId(Integer fileId) throws Exception{
        return fileService.getFileInfoByFileId(fileId);
    }
}

