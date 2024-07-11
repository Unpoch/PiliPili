package com.wz.pilipili.video.service.impl;

import com.wz.pilipili.entity.video.Area;
import com.wz.pilipili.video.mapper.AreaMapper;
import com.wz.pilipili.video.service.AreaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 分区表 服务实现类
 * </p>
 *
 * @author unkonwnzz
 * @since 2024-07-06
 */
@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService {

    /**
     * 查询所有分区
     */
    @Override
    public List<Area> getAllAreas() {
        return baseMapper.selectList(null);
    }
}
