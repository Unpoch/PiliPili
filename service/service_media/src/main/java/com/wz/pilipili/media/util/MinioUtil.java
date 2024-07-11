package com.wz.pilipili.media.util;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.wz.pilipili.exception.ConditionException;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * MinIO工具类
 */
public class MinioUtil {


    /**
     * 获取文件默认存储路径 年/月/日
     */
    public static String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date()).replace("-", "/") + "/";
    }

    /**
     * 获取文件的md5
     */
    public static String getFileMd5(MultipartFile file) throws Exception {
        InputStream fis = file.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int byteRead;
        while ((byteRead = fis.read(buffer)) > 0) {
            baos.write(buffer, 0, byteRead);
        }
        fis.close();
        return DigestUtils.md5Hex(baos.toByteArray());
    }

    /**
     * 根据文件的md5值，设置分块文件存储目录
     */
    public static String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + "chunk-";
    }

    /**
     * 获取文件上传后的url
     */
    public static String getUploadFileUrl(String minioServerUrl,String bucket, String objectName) {
        return minioServerUrl + "/" + bucket + "/" + objectName;
    }

    /**
     * 获取文件转码后，新文件的objectName
     */
    public static String getNewObjectName(String objectName, String fileName, String extension) {
        int lastIndex = objectName.lastIndexOf("/");
        String filePath = objectName.substring(0, lastIndex + 1);
        return filePath + fileName + extension;
    }

    /**
     * 获取文件上传后的url，文件转码后的文件将使用该方法上传
     */
    public static String getUploadFileUrl(String minioServerUrl,String bucket, String objectName, String extension) {
        if (objectName == null || objectName.isEmpty()) {
            throw new IllegalArgumentException("Object name cannot be null or empty");
        }
        // Ensure extension starts with a dot
        if (extension != null && !extension.startsWith(".")) {
            extension = "." + extension;
        }
        int lastSlashIndex = objectName.lastIndexOf('/');
        int lastDotIndex = objectName.lastIndexOf('.');
        // Handle cases where there is no slash or dot
        if (lastSlashIndex == -1) {
            lastSlashIndex = 0;
        } else {
            lastSlashIndex += 1;
        }
        if (lastDotIndex == -1 || lastDotIndex <= lastSlashIndex) {
            lastDotIndex = objectName.length();
        }
        String filePath = lastSlashIndex > 0 ? objectName.substring(0, lastSlashIndex - 1) : "";
        String fileNameWithoutExtension = objectName.substring(lastSlashIndex, lastDotIndex);
        return minioServerUrl + "/" + bucket + (filePath.isEmpty() ? "" : "/" + filePath) + "/" + fileNameWithoutExtension + (extension == null ? "" : extension);
    }

    /**
     * 获取没有扩展名的文件名
     */
    public static String getFileNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        return fileName.substring(0, lastDotIndex);
    }


    /**
     * 获取文件的扩展名
     */
    public static String getFileType(MultipartFile file) {
        if (file == null) {
            throw new ConditionException("非法文件！");
        }
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        int index = fileName.lastIndexOf(".");
        return fileName.substring(index + 1);
    }

    /**
     * 根据文件的扩展名获取mimeType
     */
    public static String getMimeType(String extension) {
        if (extension == null)
            extension = "";
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        //通用mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }
}
