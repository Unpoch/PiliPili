package com.wz.pilipili.repository;

import com.wz.pilipili.entity.user.UserInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserInfoRepository extends ElasticsearchRepository<UserInfo, Long> {

}
