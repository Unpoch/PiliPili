package com.wz.pilipili.gateway.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class WhiteListConfig {

    private Set<String> whiteListedPaths;

    @PostConstruct
    public void init() {
        whiteListedPaths = Collections.synchronizedSet(new HashSet<>());
        // 登录登出
        whiteListedPaths.add("/rsa-pks");
        whiteListedPaths.add("/register");
        whiteListedPaths.add("/login");
        whiteListedPaths.add("/login-dts");
        whiteListedPaths.add("/access-token");
        whiteListedPaths.add("/logout");
        // 用户
        whiteListedPaths.add("/user/viewed");
        //搜索
        whiteListedPaths.add("/getContents");
        //视频弹幕
        whiteListedPaths.add("/getVideoDanmus");
        //视频
        whiteListedPaths.add("/pageVideo");
        whiteListedPaths.add("/playVideo");
        whiteListedPaths.add("/getVideoDetails");
        //视频硬币
        whiteListedPaths.add("/coin/getVideoCoins");
        //视频分区
        whiteListedPaths.add("/area/getAllAreas");
        whiteListedPaths.add("/area/pageListAreaVideo");
        //视频分享
        whiteListedPaths.add("/share/getVideoShareCount");
        //视频点赞
        whiteListedPaths.add("/getVideoLikes");
        //视频播放记录
        whiteListedPaths.add("/getViewCounts");
    }

    public boolean isWhiteListed(String path) {
        for (String whiteListedPath : whiteListedPaths) {
            if(path.contains(whiteListedPath)) {
                return true;
            }
        }
        return false;
    }
}
