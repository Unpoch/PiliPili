package com.wz.pilipili.controller;

import com.wz.pilipili.entity.user.UserInfo;
import com.wz.pilipili.entity.video.Video;
import com.wz.pilipili.result.R;
import com.wz.pilipili.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 全文搜索
     */
    @GetMapping("/getContents")
    public R<List<Map<String, Object>>> getContents(@RequestParam String keyword,
                                                    @RequestParam Integer pageNo,
                                                    @RequestParam Integer pageSize) throws IOException {
        List<Map<String, Object>> result = searchService.getContents(keyword, pageNo, pageSize);
        return new R<>(result);
    }


    /*
    远程调用接口
    添加用户信息到ES
     */
    @PostMapping("/inner/addUserInfo")
    public void addUserInfo(@RequestBody UserInfo userInfo) {
        searchService.addUserInfo(userInfo);
    }

    /*
    远程调用接口
    添加视频信息到ES
     */
    @PostMapping("/inner/addVideo")
    public void addVideo(@RequestBody Video video) {
        searchService.addVideo(video);
    }
}
