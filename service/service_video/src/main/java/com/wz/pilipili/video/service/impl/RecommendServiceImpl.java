package com.wz.pilipili.video.service.impl;

import com.wz.pilipili.constant.RecommendConstant;
import com.wz.pilipili.entity.video.Video;
import com.wz.pilipili.exception.ConditionException;
import com.wz.pilipili.video.service.RecommendService;
import com.wz.pilipili.video.service.UserPreferenceService;
import com.wz.pilipili.video.service.VideoService;
import com.wz.pilipili.vo.user.UserPreference;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐视频服务
 */
@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserPreferenceService userPreferenceService;

    /**
     * 视频内容推荐
     */
    @Override
    public List<Video> getVideoRecommendations(Long userId, String recommendType) {
        List<Video> result = new ArrayList<>();
        try {
            //如果是基于用户做推荐
            if (RecommendConstant.RECOMMEND_TYPE_USER.equals(recommendType)) {
                result = this.recommendByUser(userId, RecommendConstant.DEFAULT_RECOMMEND_NUMBER);
            } else {
                //找到用户最喜欢的视频，作为推荐的基础内容
                List<UserPreference> preferencesList = userPreferenceService.getAllUserPreference();
                Optional<Long> itemIdOpt = preferencesList.stream().filter(item -> item.getUserId().equals(userId))
                        .max(Comparator.comparing(UserPreference::getValue)).map(UserPreference::getVideoId);
                if (itemIdOpt.isPresent()) {
                    result = this.recommendByContent(userId, itemIdOpt.get(), RecommendConstant.DEFAULT_RECOMMEND_NUMBER);
                }
            }
            //若没有计算出推荐内容，则默认查询最新视频
            if (recommendType.isEmpty()) {
                result = videoService.pageListVideos(3, 1, null).getList();
            }
        } catch (Exception e) {
            throw new ConditionException("推荐失败！");
        }
        return result;
    }

    /*
    基于用户的协同推荐
     */
    private List<Video> recommendByUser(Long userId, int recommendCounts) throws Exception {
        List<UserPreference> list = userPreferenceService.getAllUserPreference();
        //创建数据模型
        DataModel dataModel = this.createDataModel(list);
        //获取用户相似程度
        UserSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
        System.out.println(similarity.userSimilarity(11, 12));
        //获取用户邻居
        UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(2, similarity, dataModel);
        long[] ar = userNeighborhood.getUserNeighborhood(userId);
        //构建推荐器
        Recommender recommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, similarity);
        //推荐视频
        List<RecommendedItem> recommendedItems = recommender.recommend(userId, recommendCounts);
        Set<Long> itemIds = recommendedItems.stream().map(RecommendedItem::getItemID).collect(Collectors.toSet());
        return videoService.getVideoListByVideoIds(itemIds);
    }

    /*
    基于内容的协同推荐
     */
    private List<Video> recommendByContent(Long userId, Long itemId, int recommendCounts) throws Exception {
        List<UserPreference> list = userPreferenceService.getAllUserPreference();
        //创建数据模型
        DataModel dataModel = this.createDataModel(list);
        //获取内容相似程度
        ItemSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
        GenericItemBasedRecommender genericItemBasedRecommender = new GenericItemBasedRecommender(dataModel, similarity);
        // 物品推荐相拟度，计算两个物品同时出现的次数，次数越多任务的相拟度越高
        Set<Long> itemIds = genericItemBasedRecommender.recommendedBecause(userId, itemId, recommendCounts)
                .stream()
                .map(RecommendedItem::getItemID)
                .collect(Collectors.toSet());
        //推荐视频
        return videoService.getVideoListByVideoIds(itemIds);
    }

    /*
    根据用户偏好创建数据模型
     */
    private DataModel createDataModel(List<UserPreference> userPreferenceList) {
        FastByIDMap<PreferenceArray> fastByIdMap = new FastByIDMap<>();
        Map<Long, List<UserPreference>> map = userPreferenceList.stream().collect(Collectors.groupingBy(UserPreference::getUserId));
        Collection<List<UserPreference>> list = map.values();
        for (List<UserPreference> userPreferences : list) {
            GenericPreference[] array = new GenericPreference[userPreferences.size()];
            for (int i = 0; i < userPreferences.size(); i++) {
                UserPreference userPreference = userPreferences.get(i);
                GenericPreference item = new GenericPreference(userPreference.getUserId(), userPreference.getVideoId(), userPreference.getValue());
                array[i] = item;
            }
            fastByIdMap.put(array[0].getUserID(), new GenericUserPreferenceArray(Arrays.asList(array)));
        }
        return new GenericDataModel(fastByIdMap);
    }
}
