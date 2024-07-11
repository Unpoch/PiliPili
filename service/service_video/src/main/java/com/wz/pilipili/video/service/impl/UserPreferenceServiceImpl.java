package com.wz.pilipili.video.service.impl;

import com.wz.pilipili.video.service.UserPreferenceService;
import com.wz.pilipili.video.service.VideoOperationService;
import com.wz.pilipili.vo.user.UserPreference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPreferenceServiceImpl implements UserPreferenceService {


    @Autowired
    private VideoOperationService videoOperationService;

    /**
     * 查询所有 用户偏好
     */
    @Override
    public List<UserPreference> getAllUserPreference() {
        return videoOperationService.getAllUserPreference();
    }
}
