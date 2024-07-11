package com.wz.pilipili.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wz.pilipili.entity.video.VideoOperation;
import com.wz.pilipili.vo.user.UserPreference;

import java.util.List;

/**
 * <p>
 * 视频操作表 Mapper 接口
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-06-26
 */
public interface VideoOperationMapper extends BaseMapper<VideoOperation> {

    List<UserPreference> getAllUserPreference();
}
