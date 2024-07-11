package com.wz.pilipili.repository;

import com.wz.pilipili.entity.video.Video;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * VideoRepository
 */
public interface VideoRepository extends ElasticsearchRepository<Video, Long> {


    List<Video> findByTitleLike(String keyword);
}
