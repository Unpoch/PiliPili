package com.wz.pilipili.service;

import com.wz.pilipili.entity.user.UserInfo;
import com.wz.pilipili.entity.video.Video;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SearchService {

    List<Map<String, Object>> getContents(String keyword,
                                          Integer pageNo,
                                          Integer pageSize) throws IOException;

    void addVideo(Video video);

    void addUserInfo(UserInfo userInfo);

}
