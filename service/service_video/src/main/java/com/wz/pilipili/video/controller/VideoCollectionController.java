package com.wz.pilipili.video.controller;


import com.wz.pilipili.context.UserContext;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.video.Video;
import com.wz.pilipili.entity.video.VideoCollection;
import com.wz.pilipili.entity.video.VideoCollectionGroup;
import com.wz.pilipili.result.R;
import com.wz.pilipili.video.service.VideoCollectionGroupService;
import com.wz.pilipili.video.service.VideoCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 视频收藏记录表 前端控制器
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-17
 */
@RestController
@RequestMapping("/video/collection")
public class VideoCollectionController {

    @Autowired
    private VideoCollectionService videoCollectionService;


    /**
     * 收藏视频
     */
    @PostMapping("/collectVideo")
    public R<String> collectVideo(@RequestBody VideoCollection videoCollection) {
        Long userId = UserContext.getCurUserId();
        videoCollectionService.collectVideo(userId, videoCollection);
        return R.success();
    }

    /**
     * 更新收藏视频
     */
    @PostMapping("/updateVideoCollection")
    public R<String> updateVideoCollection(@RequestBody VideoCollection videoCollection) {
        Long userId = UserContext.getCurUserId();
        videoCollectionService.updateVideoCollection(userId, videoCollection);
        return R.success();
    }

    /**
     * 取消收藏视频
     */
    @PostMapping("/deleteVideoCollection")
    public R<String> cancelCollectVideo(@RequestParam Long videoId) {
        Long userId = UserContext.getCurUserId();
        videoCollectionService.cancelCollectVideo(userId, videoId);
        return R.success();
    }

    /**
     * 查询视频收藏数量
     * 游客模式也可以查看
     */
    @GetMapping("/getCollections")
    public R<Map<String, Object>> getVideoCollections(@RequestParam Long videoId) {
        Long userId = UserContext.getCurUserId();
        Map<String, Object> result = videoCollectionService.getVideoCollections(userId, videoId);
        return new R<>(result);
    }


    /**
     * 添加视频收藏分组
     * TODO ：应该将分组id groupId返回
     */
    @PostMapping("/addCollectionGroup")
    public R<String> addVideoCollectionGroup(@RequestBody VideoCollectionGroup videoCollectionGroup) {
        Long userId = UserContext.getCurUserId();
        videoCollectionService.addVideoCollectionGroup(userId, videoCollectionGroup);
        return R.success();
    }


    /**
     * 删除视频收藏分组
     */
    @PostMapping("/deleteCollectionGroup")
    public R<String> deleteCollectionGroup(@RequestParam Long groupId) {
        Long userId = UserContext.getCurUserId();
        videoCollectionService.deleteCollectionGroup(userId, groupId);
        return R.success();
    }

    /**
     * 更新收藏夹（改名）
     */
    @PostMapping("/updateCollectionGroup")
    public R<String> updateCollectionGroup(@RequestBody VideoCollectionGroup videoCollectionGroup) {
        Long userId = UserContext.getCurUserId();
        videoCollectionService.updateCollectionGroup(userId, videoCollectionGroup);
        return R.success();
    }


    /**
     *  分页查看收藏分组下所有视频（用户查看自己的）
     *  返回VideoCollection是因为冗余了Video字段
     */
    @GetMapping("/pageGroupVideos")
    public R<PageResult<VideoCollection>> pageGroupVideos(@RequestParam Integer no,
                                                @RequestParam Integer size,
                                                @RequestParam Integer groupId) {
        Long userId = UserContext.getCurUserId();
        PageResult<VideoCollection> pageResult = videoCollectionService.pageGroupVideos(no, size, groupId, userId);
        return new R<>(pageResult);
    }

    //TODO: 当前用户，查看其他用户的收藏夹
    //...
}

