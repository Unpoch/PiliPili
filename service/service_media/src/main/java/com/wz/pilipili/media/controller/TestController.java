package com.wz.pilipili.media.controller;

import com.wz.pilipili.exception.ConditionException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

@RestController
@RequestMapping("/test")
public class TestController {


    private static final int SLICE_SIZE = 1024 * 1024 * 5;

    @PostMapping("/chunks")
    public void convertToChunks(MultipartFile multipartFile) throws Exception {
        String fileType = this.getFileType(multipartFile);
        //生成临时文件，将MultipartFile转为File
        File file = multipartFileToFile(multipartFile);
        long fileLength = file.length();
        int count = 1;
        for (int i = 0; i < fileLength; i += SLICE_SIZE) {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.seek(i);
            byte[] bytes = new byte[SLICE_SIZE];
            int len = randomAccessFile.read(bytes);
            String path = "D:\\fileChunks\\" + count + "." + fileType;
            File slice = new File(path);
            FileOutputStream fos = new FileOutputStream(slice);
            fos.write(bytes, 0, len);
            fos.close();
            randomAccessFile.close();
            count++;
        }
        //删除临时文件
        file.delete();
    }

    public File multipartFileToFile(MultipartFile multipartFile) throws Exception {
        String originalFileName = multipartFile.getOriginalFilename();
        String[] fileName = originalFileName.split("\\.");
        String prefix = fileName[0].length() >= 3 ? fileName[0] : fileName[0] + "tmp";
        String suffix = fileName.length > 1 ? "." + fileName[1] : ".tmp";
        File file = File.createTempFile(prefix, suffix);
        multipartFile.transferTo(file);
        return file;
    }


    public String getFileType(MultipartFile file) {
        if (file == null) {
            throw new ConditionException("非法文件！");
        }
        String fileName = file.getOriginalFilename();
        int index = fileName.lastIndexOf(".");
        return fileName.substring(index + 1);
    }
}
