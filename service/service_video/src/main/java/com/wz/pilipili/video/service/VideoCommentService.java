package com.wz.pilipili.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.video.VideoComment;

/**
 * <p>
 * 视频评论表 服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-17
 */
public interface VideoCommentService extends IService<VideoComment> {

    void addVideoComment(Long userId, VideoComment videoComment);

    PageResult<VideoComment> pageVideoComments(Integer no, Integer size, Long videoId);
}
