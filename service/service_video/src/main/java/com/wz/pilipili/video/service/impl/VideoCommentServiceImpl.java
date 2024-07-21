package com.wz.pilipili.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wz.pilipili.annotation.ApiLimitedRole;
import com.wz.pilipili.constant.AuthRoleConstant;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.user.UserInfo;
import com.wz.pilipili.entity.video.Video;
import com.wz.pilipili.entity.video.VideoComment;
import com.wz.pilipili.exception.ConditionException;
import com.wz.pilipili.user.client.UserInfoFeignClient;
import com.wz.pilipili.video.mapper.VideoCommentMapper;
import com.wz.pilipili.video.service.VideoCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wz.pilipili.video.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 视频评论表 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-17
 */
@Service
public class VideoCommentServiceImpl extends ServiceImpl<VideoCommentMapper, VideoComment> implements VideoCommentService {

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    @Autowired
    private VideoService videoService;

    /**
     * 添加视频评论
     */
    @Override
    public void addVideoComment(Long userId, VideoComment videoComment) {
        Long videoId = videoComment.getVideoId();
        if (videoId == null) {
            throw new ConditionException("参数异常");
        }
        Video video = videoService.getVideoByVideoId(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！");
        }
        videoComment.setUserId(userId);
        baseMapper.insert(videoComment);
    }

    /**
     * 分页查询视频评论
     */
    @Override
    public PageResult<VideoComment> pageVideoComments(Integer no, Integer size, Long videoId) {
        //1.获取分页参数，封装分页参数对象，进行查询
        int start = (no - 1) * size;
        int limit = size;
        Page<VideoComment> pageParams = new Page<>(start, limit);
        //2. 先查询所有一级评论（rootId == null）
        IPage<VideoComment> parentCommentPage = baseMapper.selectPage(pageParams, new LambdaQueryWrapper<VideoComment>()
                .eq(VideoComment::getVideoId, videoId)
                .eq(VideoComment::getRootId, null)
                .orderByDesc(VideoComment::getId));
        List<VideoComment> parentCommentList = parentCommentPage.getRecords();
        long total = parentCommentPage.getTotal();
        //2.1 筛选出一级评论id集合 / userId集合 / replyUserId是不需要的通过一级评论进行筛选的，由二级评论列表进行筛选
        Set<Long> parentCommentIds = parentCommentList.stream().map(VideoComment::getId).collect(Collectors.toSet());
        Set<Long> userIds = parentCommentList.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
        // Set<Long> parentCommentReplyUserIds = parentCommentList.stream().map(VideoComment::getReplyUserId).collect(Collectors.toSet());
        //3. 根据一级评论 批量查询二级评论（因为一级评论对应着它的二级评论列表）
        List<VideoComment> childCommentList = baseMapper.selectList(new LambdaQueryWrapper<VideoComment>()
                .eq(VideoComment::getVideoId, videoId)
                .in(VideoComment::getRootId, parentCommentIds));
        //3.1 筛选二级评论的userId 和 replyUserId
        Set<Long> childCommentUserIds = childCommentList.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
        Set<Long> childCommentReplyUserIds = childCommentList.stream().map(VideoComment::getReplyUserId).collect(Collectors.toSet());
        //3.2 将parentCommentUserIds 和childCommentUserIds ，childCommentReplyUserIds合并成一个set，查询相关的所有用户信息
        userIds.addAll(childCommentUserIds);
        userIds.addAll(childCommentReplyUserIds);
        List<UserInfo> userInfoList = userInfoFeignClient.batchGetUserInfoListByUserIds(userIds);
        //为了在循环中方便设置userInfo,replyUserInfo，将userInfoList 变成Map（key为userId，value为UserInfo）
        Map<Long, UserInfo> userInfoMap = userInfoList.stream().collect(Collectors.toMap(UserInfo::getUserId, item -> item));//item表示对应的UserInfo对象
        //4.为一级评论设置它的二级评论子列表（根据rootId进行对应）
        //  为所有的VideoComment（不管是一级评论还是二级评论），设置userInfo,replyUserInfo
        parentCommentList.forEach(parentComment -> {
            Long rootId = parentComment.getId();
            List<VideoComment> childList = new ArrayList<>();
            childCommentList.forEach(childComment -> {
                if (rootId.equals(childComment.getRootId())) {//是该评论的二级评论
                    childList.add(childComment);
                    childComment.setUserInfo(userInfoMap.get(childComment.getUserId()));//设置userInfo
                    childComment.setReplyUserInfo(userInfoMap.get(childComment.getReplyUserId()));//设置replyUserInfo
                }
            });
            //设置一级评论的二级评论列表
            parentComment.setChildList(childList);
            //设置一级评论的UserInfo
            parentComment.setUserInfo(userInfoMap.get(parentComment.getUserId()));
            //这里不需要设置一级评论的replyUserInfo，因此这是由它的子列表完成的
        });
        return new PageResult<>(total, parentCommentList);
    }
}
