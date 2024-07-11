package com.wz.pilipili.video.service.impl;

import com.wz.pilipili.entity.video.VideoOperation;
import com.wz.pilipili.video.mapper.VideoOperationMapper;
import com.wz.pilipili.video.service.VideoOperationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wz.pilipili.vo.user.UserPreference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 视频操作表 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-26
 */
@Service
public class VideoOperationServiceImpl extends ServiceImpl<VideoOperationMapper, VideoOperation> implements VideoOperationService {

    @Autowired
    private VideoOperationMapper videoOperationMapper;

    /**
     * 查询所有用户偏好
     */
    @Override
    public List<UserPreference> getAllUserPreference() {
        return videoOperationMapper.getAllUserPreference();
    }
}
