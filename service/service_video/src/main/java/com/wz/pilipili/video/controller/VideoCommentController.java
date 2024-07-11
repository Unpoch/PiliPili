package com.wz.pilipili.video.controller;


import com.wz.pilipili.context.UserContext;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.video.VideoComment;
import com.wz.pilipili.result.R;
import com.wz.pilipili.video.service.VideoCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 视频评论表 前端控制器
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-17
 */
@RestController
@RequestMapping("/video/comment")
public class VideoCommentController {

    @Autowired
    private VideoCommentService videoCommentService;

    /**
     * 添加视频评论
     */
    @PostMapping("/addVideoComment")
    public R<String> addVideoComment(@RequestBody VideoComment videoComment) {
        Long userId = UserContext.getCurUserId();
        videoCommentService.addVideoComment(userId, videoComment);
        return R.success();
    }

    /**
     * 分页查询视频评论
     */
    @GetMapping("/pageVideoComments")
    public R<PageResult<VideoComment>> pageVideoComments(@RequestParam Integer no,
                                                         @RequestParam Integer size,
                                                         @RequestParam Long videoId) {
        PageResult<VideoComment> pageResult = videoCommentService.pageVideoComments(no, size, videoId);
        return new R<>(pageResult);
    }
}

