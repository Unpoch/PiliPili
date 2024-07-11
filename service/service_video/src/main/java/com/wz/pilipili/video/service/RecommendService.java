package com.wz.pilipili.video.service;

import com.wz.pilipili.entity.video.Video;

import java.util.List;

public interface RecommendService {
    List<Video> getVideoRecommendations(Long userId, String recommendType);
}
