package com.wz.pilipili.media.util;

import com.wz.pilipili.exception.ConditionException;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 将视频转为MP4格式工具类
 */
public class MP4VideoUtil extends VideoUtil {


    /**
     * 清除已生成的mp4
     */
    public static void clearMp4(String mp4Path) {
        //删除原来已经生成的m3u8及ts文件
        File mp4File = new File(mp4Path);
        if (mp4File.exists() && mp4File.isFile()) {
            mp4File.delete();
        }
    }

    /**
     * 视频编码，生成mp4文件
     *
     * @return 成功返回success，失败返回控制台日志
     */
    public static String generateMp4(String ffmpegPath, String inputFilePath, String outputFolderPath, String outputFileName) {
        // 清除已生成的 mp4 文件
        clearMp4(outputFolderPath + outputFileName);
        List<String> command = new ArrayList<>();
        command.add(ffmpegPath);  // FFmpeg 可执行文件的路径
        command.add("-i");
        command.add(inputFilePath);
        command.add("-c:v");
        command.add("libx264");
        command.add("-y"); // 覆盖输出文件
        command.add("-s");
        command.add("1280x720");
        command.add("-pix_fmt");
        command.add("yuv420p");
        command.add("-b:a");
        command.add("63k");
        command.add("-b:v");
        command.add("753k");
        command.add("-r");
        command.add("18");
        command.add(outputFolderPath + outputFileName);
        String outstring = null;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(command);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            // 读取进程的输出信息
            outstring = waitFor(ffmpegPath, process);
            // 检查视频时间
            Boolean checkVideoTime = checkVideoTime(ffmpegPath, inputFilePath, outputFolderPath + outputFileName);
            if (!checkVideoTime) {
                return outstring;
            } else {
                return "success";
            }
        } catch (IOException e) {
            throw new ConditionException("视频转码失败！");
        }
    }

    public static void main(String[] args) throws IOException {


        //ffmpeg的路径
        String ffmpeg_path = "D:\\Software\\ffmpeg\\bin\\ffmpeg.exe";//ffmpeg的安装位置
        //源avi视频的路径
        String video_path = "D:\\Desktop\\Git-你工作中Git出现了冲突是如何解决的？.wmv";
        //转换后mp4文件的名称
        String mp4_name = "Git.mp4";
        //转换后mp4文件的路径
        String mp4_path = "D:\\Desktop\\Git.mp4";
        //创建工具类对象
        //开始视频转换，成功将返回success
        String s = MP4VideoUtil.generateMp4(ffmpeg_path, video_path, mp4_path, mp4_name);
        System.out.println(s);
    }

}