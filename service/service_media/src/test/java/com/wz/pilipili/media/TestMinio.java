package com.wz.pilipili.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import io.minio.errors.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
public class TestMinio {

    @Autowired
    private MinioClient minioClient;

    @Test
    public void upload() {
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".txt");
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//通用mimeType，字节流
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        try {
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket("testbucket")
                    .object("001/test001.txt")//添加子目录
                    .filename("D:\\recovery-codes.txt")
                    .contentType(mimeType)//默认根据扩展名确定文件内容类型，也可以指定
                    .build();
            minioClient.uploadObject(testbucket);
            System.out.println("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("上传失败");
        }
    }

    /**
     * 删除文件
     */
    @Test
    public void delete() {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket("testbucket").object("001/test001.txt").build());
            System.out.println("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("删除失败");
        }
    }

    //查询文件 从minio中下载
    @Test
    public void test_getFile() throws Exception {

        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket("testbucket").object("test/01/1.mp4").build();
        //查询远程服务获取到一个流对象
        FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
        //指定输出流
        FileOutputStream outputStream = new FileOutputStream(new File("D:\\develop\\upload\\1a.mp4"));
        IOUtils.copy(inputStream, outputStream);

        //校验文件的完整性对文件的内容进行md5
        FileInputStream fileInputStream1 = new FileInputStream(new File("D:\\develop\\upload\\1.mp4"));
        String source_md5 = DigestUtils.md5Hex(fileInputStream1);
        FileInputStream fileInputStream = new FileInputStream(new File("D:\\develop\\upload\\1a.mp4"));
        String local_md5 = DigestUtils.md5Hex(fileInputStream);
        if (source_md5.equals(local_md5)) {
            System.out.println("下载成功");
        }

    }


    //将分块文件上传到minio
    @Test
    public void uploadChunk() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        for (int i = 0; i < 6; i++) {
            //上传文件的参数信息
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket("testbucket")//桶
                    .filename("D:\\develop\\upload\\chunk\\" + i) //指定本地文件路径
                    .object("chunk/" + i)//对象名 放在子目录下
                    .build();

            //上传文件
            minioClient.uploadObject(uploadObjectArgs);
            System.out.println("上传分块" + i + "成功");
        }

    }

    //调用minio接口合并分块
    @Test
    public void testMerge() throws Exception {

//        List<ComposeSource> sources = new ArrayList<>();
//        for (int i = 0; i < 30; i++) {
//            //指定分块文件的信息
//            ComposeSource composeSource = ComposeSource.builder().bucket("testbucket").object("chunk/" + i).build();
//            sources.add(composeSource);
//        }

        List<ComposeSource> sources = Stream.iterate(0, i -> ++i).limit(6).map(i -> ComposeSource.builder().bucket("testbucket").object("chunk/" + i).build()).collect(Collectors.toList());

        //指定合并后的objectName等信息
        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .bucket("testbucket")
                .object("merge01.mp4")
                .sources(sources)//指定源文件
                .build();
        //合并文件,
        //报错size 1048576 must be greater than 5242880，minio默认的分块文件大小为5M
        minioClient.composeObject(composeObjectArgs);

    }

}
