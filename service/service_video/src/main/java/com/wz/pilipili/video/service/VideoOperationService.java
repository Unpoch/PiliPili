package com.wz.pilipili.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.video.VideoOperation;
import com.wz.pilipili.vo.user.UserPreference;

import java.util.List;

/**
 * <p>
 * 视频操作表 服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-26
 */
public interface VideoOperationService extends IService<VideoOperation> {

    List<UserPreference> getAllUserPreference();
}
