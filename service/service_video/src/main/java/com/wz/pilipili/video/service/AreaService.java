package com.wz.pilipili.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wz.pilipili.entity.video.Area;

import java.util.List;

/**
 * <p>
 * 分区表 服务类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-07-06
 */
public interface AreaService extends IService<Area> {

    List<Area> getAllAreas();
}
