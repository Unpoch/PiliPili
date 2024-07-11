package com.wz.pilipili.service.impl;

import com.wz.pilipili.entity.user.UserInfo;
import com.wz.pilipili.entity.video.Video;
import com.wz.pilipili.repository.UserInfoRepository;
import com.wz.pilipili.repository.VideoRepository;
import com.wz.pilipili.service.SearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    /**
     * 全文搜索
     */
    @Override
    public List<Map<String, Object>> getContents(String keyword,
                                                 Integer pageNo,
                                                 Integer pageSize) throws IOException {
        String[] indices = {"videos", "user-infos"};//要搜索的索引库
        SearchRequest searchRequest = new SearchRequest(indices);//构建搜索请求
        //创建SourceBuilder
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(pageNo - 1);//分页
        sourceBuilder.size(pageSize);
        //构建多条件的查询器 MultiMatchQueryBuilder
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "title", "description", "nick");
        sourceBuilder.query(matchQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        //高亮显示HighlightBuilder
        String[] array = {"title", "description", "nick"};//需要高亮的三个字段
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        for (String key : array) {
            highlightBuilder.fields().add(new HighlightBuilder.Field(key));
        }
        highlightBuilder.requireFieldMatch(false);//多个字段的高亮，要把这个值设置为false
        highlightBuilder.preTags("<span style=\"color:red\">");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);
        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits()) {
            //处理高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            Map<String, Object> sourceMap = hit.getSourceAsMap();
            for (String key : array) {
                HighlightField field = highlightFields.get(key);
                if (field != null) {
                    Text[] fragments = field.getFragments();
                    String str = Arrays.toString(fragments);
                    str = str.substring(1, str.length() - 1);
                    sourceMap.put(key, str);
                }
            }
            result.add(sourceMap);
        }
        return result;
    }


    /**
     * 将Video添加到ES中
     */
    @Override
    public void addVideo(Video video) {
        videoRepository.save(video);
    }


    /**
     * 将UserInfo添加到ES中
     */
    @Override
    public void addUserInfo(UserInfo userInfo) {
        userInfoRepository.save(userInfo);
    }


}
