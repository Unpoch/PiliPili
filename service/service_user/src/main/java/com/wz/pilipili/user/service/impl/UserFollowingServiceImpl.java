package com.wz.pilipili.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wz.pilipili.constant.UserConstant;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.user.FollowingGroup;
import com.wz.pilipili.entity.user.User;
import com.wz.pilipili.entity.user.UserFollowing;
import com.wz.pilipili.entity.user.UserInfo;
import com.wz.pilipili.exception.ConditionException;
import com.wz.pilipili.user.mapper.UserFollowingMapper;
import com.wz.pilipili.user.service.FollowingGroupService;
import com.wz.pilipili.user.service.UserFollowingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wz.pilipili.user.service.UserInfoService;
import com.wz.pilipili.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户关注表 服务实现类
 *
 * @author unkonwnzz
 * @since 2024-05-28
 */
@Service
public class UserFollowingServiceImpl extends ServiceImpl<UserFollowingMapper, UserFollowing> implements UserFollowingService {

    @Autowired
    private FollowingGroupService followingGroupService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoService userInfoService;


    /**
     * 添加用户关注
     */
    @Override
    @Transactional //防止删除成功，插入失败..
    public void follow(UserFollowing userFollowing) {
        //1.通过userFollowing获取groupId，
        // 如果没有，先获取默认分组，然后设置userFollowing的groupId
        Long groupId = userFollowing.getGroupId();
        if (groupId == null) {//放到默认分组中
            FollowingGroup defaultGroup = followingGroupService.getByType(UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            userFollowing.setGroupId(defaultGroup.getId());//需要设置
        } else {//2.如果groupId存在，通过groupId查询出FollowingGroup，判断是否为空，空则说明分组不存在
            FollowingGroup followingGroup = followingGroupService.getById(groupId);
            if (followingGroup == null) {
                throw new ConditionException("分组不存在！");
            }
        }
        //3.判断关注的人（通过followingId）是否存在，不存在也要抛异常
        Long followingId = userFollowing.getFollowingId();
        User dbUser = userService.getById(followingId);
        if (dbUser == null) {
            throw new ConditionException("关注的用户不存在！");
        }
        //4.删除userId和followingId之间的关联关系
        Long userId = userFollowing.getUserId();
        baseMapper.delete(new LambdaQueryWrapper<UserFollowing>()
                .eq(UserFollowing::getFollowingId, followingId)
                .eq(UserFollowing::getUserId, userId));
        //5.插入userId和followingId之间的关联关系
        baseMapper.insert(userFollowing);
    }

    /**
     * 获取关注列表
     */
    @Override
    public List<FollowingGroup> getFollowingList(Long userId) {
        //1.获取用户的关注列表 id
        List<UserFollowing> followingList = this.getUserFollowings(userId);
        Set<Long> followIds = followingList.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        //2.根据followingId查询其基本信息（使用followingId查询对应的UserInfo）
        // followIds => UserInfo的list，然后设置到每个UserFollowing的userinfo字段里
        if (!followIds.isEmpty()) {
            userInfoList = userInfoService.getUserInfoListByUserIds(followIds);
        }
        for (UserFollowing userFollowing : followingList) {
            for (UserInfo userInfo : userInfoList) {
                //找到UserFollowing 的 followingId 对应的 UserInfo
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }
        //3.将用户的关注 按 关注分组进行分类
        //3.1 先根据userId 的所有分组列表list
        //3.2 新建一个叫"全部关注"的分组allGroup，将list设置到 该分组的 List<UserInfo>属性中（这个"全部关注"分组是前端需要的数据）
        //    将该"全部关注"分组加入到结果列表中（分组列表）
        //3.3 为设置每个分组的FollowingGroup的 List<UserInfo> 属性，创建一个列表infos
        //    需要followingList的每一个UserFollow的 groupId 和 FollowingGroup的 id进行匹配
        //     如果匹配上了，说明是一个组，将UserFollow的UserInfo 加入到infos中
        List<FollowingGroup> followingGroupList = followingGroupService.getFollowingGroupListByUserId(userId);
        FollowingGroup allGroup = new FollowingGroup();//全部分组
        allGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        allGroup.setFollowingUserInfoList(userInfoList);//全部分组对应了 用户全部关注的信息

        List<FollowingGroup> result = new ArrayList<>();
        result.add(allGroup);

        for (FollowingGroup group : followingGroupList) {
            List<UserInfo> infos = new ArrayList<>();
            for (UserFollowing userFollowing : followingList) {
                if (userFollowing.getGroupId().equals(group.getId())) {//同一个组
                    infos.add(userFollowing.getUserInfo());
                }
            }
            group.setFollowingUserInfoList(infos);//设置 分组下的关注用户列表
            result.add(group);//加入结果列表
        }
        return result;
    }

    /**
     * 根据userId获取用户粉丝列表
     */
    @Override
    public List<UserFollowing> getFanList(Long userId) {
        //1.获取当前用户的粉丝列表
        //  筛选出粉丝id集合
        List<UserFollowing> fanList = this.getUserFans(userId);
        Set<Long> fanIds = fanList.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());
        //2.根据粉丝的用户id查询基本信息
        List<UserInfo> fanUserInfoList = new ArrayList<>();
        if (!fanIds.isEmpty()) {
            fanUserInfoList = userInfoService.getUserInfoListByUserIds(fanIds);
        }
        //3.查询当前用户是否已经关注该粉丝（互关）
        //  3.1 设置fanList中每个 UserFollowing的UserInfo属性
        //  3.2 先查询用户的所有关注列表，然后用该关注列表和粉丝列表匹配 得到是否互关
        List<UserFollowing> followingList = this.getUserFollowings(userId);
        for (UserFollowing fan : fanList) {
            for (UserInfo userInfo : fanUserInfoList) {
                if (fan.getUserId().equals(userInfo.getUserId())) {//设置UserInfo属性
                    userInfo.setFollowed(false);//先设置为未关注
                    fan.setUserInfo(userInfo);
                }
            }
            //设置关注属性followed
            for (UserFollowing follow : followingList) {
                if (follow.getFollowingId().equals(fan.getUserId())) {//我的关注是我的粉丝，那么就是互关
                    fan.getUserInfo().setFollowed(true);
                }
            }
        }
        return fanList;
    }

    /**
     * 检查用户userId和 查询出的用户列表的 用户之间的关注关系
     */
    @Override
    public List<UserInfo> checkFollowingStatus(List<UserInfo> userInfoList, Long userId) {
        //1.获取用户的关注列表
        List<UserFollowing> followingList = this.getUserFollowings(userId);
        //2.遍历 查询的用户列表 根据 关注列表设置关注关系
        for (UserInfo userInfo : userInfoList) {
            userInfo.setFollowed(false);
            for (UserFollowing userFollowing : followingList) {
                //当前用户所关注的人的id = 查询的用户列表中的用户id
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    userInfo.setFollowed(true);//说明我关注了他
                }
            }
        }
        return userInfoList;
    }

    /**
     * 取关
     */
    @Override
    public void cancelFollow(Long userId, Long followingId) {
        //删除记录
        baseMapper.delete(new LambdaQueryWrapper<UserFollowing>()
                .eq(UserFollowing::getUserId, userId)
                .eq(UserFollowing::getFollowingId, followingId));
    }

    /**
     * 更新用户关注
     */
    @Override
    public void updateUserFollowing(UserFollowing userFollowing) {
        baseMapper.update(userFollowing, new LambdaQueryWrapper<UserFollowing>()
                .eq(UserFollowing::getUserId, userFollowing.getUserId())
                .eq(UserFollowing::getFollowingId, userFollowing.getFollowingId()));
    }

    /**
     * 添加关注分组
     */
    @Override
    public void addFollowingGroup(FollowingGroup followingGroup) {
        followingGroupService.addFollowingGroup(followingGroup);
    }

    /**
     * 获取所有关注分组
     */
    @Override
    public List<FollowingGroup> getAllFollowingGroups(Long userId) {
        return followingGroupService.getFollowingGroupListByUserId(userId);
    }

    /**
     * 删除关注分组
     */
    @Override
    @Transactional
    public void removeFollowingGroup(Long userId, Long groupId) {
        //1.删除关注分组记录
        followingGroupService.removeFollowingGroup(userId, groupId);
        //2.取关该分组下所有关注人
        baseMapper.delete(new LambdaQueryWrapper<UserFollowing>()
                .eq(UserFollowing::getUserId, userId)
                .eq(UserFollowing::getGroupId, groupId));
    }

    /**
     * 分页查询用户关注列表
     */
    @Override
    public PageResult<UserFollowing> pageListUserFollowings(Integer pageNo, Integer pageSize, Long userId, Long groupId) {
        //1.封装分页参数
        int start = (pageNo - 1) * pageSize;
        Page<UserFollowing> pageParams = new Page<>(start, pageSize);
        //2.调用分页方法
        IPage<UserFollowing> page = baseMapper.selectPage(pageParams, new LambdaQueryWrapper<UserFollowing>()
                .eq(UserFollowing::getUserId, userId)
                .eq(groupId != null, UserFollowing::getGroupId, groupId) //groupId != null 才添加该条件
                .orderByDesc(UserFollowing::getId));
        //3.UserFollowing中需要设置属性UserInfo
        List<UserFollowing> userFollowingList = page.getRecords();//用户的关注列表
        Set<Long> followIds = userFollowingList.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        List<UserInfo> followUserInfoList = userInfoService.getUserInfoListByUserIds(followIds);
        //获取key ：followingId， value ：UserInfo
        Map<Long, UserInfo> userInfoMap = followUserInfoList.stream().collect(Collectors.toMap(UserInfo::getUserId, item -> item));
        userFollowingList.forEach(userFollowing -> {
            userFollowing.setUserInfo(userInfoMap.get(userFollowing.getFollowingId()));
        });
        //4.返回PageResult对象
        return new PageResult<>(page.getTotal(), userFollowingList);
    }

    /**
     * 分页查询用户粉丝列表
     */
    @Override
    public PageResult<UserFollowing> pageListUserFans(Integer pageNo, Integer pageSize, Long userId) {
        //1.封装分页参数
        int start = (pageNo - 1) * pageSize;
        Page<UserFollowing> pageParams = new Page<>(start, pageSize);
        //2.调用分页方法
        IPage<UserFollowing> page = baseMapper.selectPage(pageParams, new LambdaQueryWrapper<UserFollowing>()
                .eq(UserFollowing::getUserId, userId)
                .orderByDesc(UserFollowing::getId));
        //3.封装UserFollowing的UserInfo属性
        //4.设置和粉丝的互关属性
        List<UserFollowing> userFanList = page.getRecords();//用户的粉丝列表 userId就代表粉丝的id
        List<UserFollowing> userFollowingList = this.getUserFollowings(userId);//用户的关注列表
        Set<Long> fanIds = userFanList.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());
        List<UserInfo> fanUserInfoList = userInfoService.getUserInfoListByUserIds(fanIds);
        userFanList.forEach(fan -> {
            //遍历fanUserInfoList设置互关属性
            fanUserInfoList.forEach(userInfo -> {
                if (fan.getUserId().equals(userInfo.getUserId())) {//说明这是fan对应的UserInfo
                    //查询用户的关注列表中有没有这个粉丝
                    boolean followed = userFollowingList.stream()
                            .anyMatch(item ->
                                    item.getFollowingId().equals(fan.getUserId()));//你的关注人同时是你的粉丝
                    userInfo.setFollowed(followed);
                    //设置userInfo属性
                    fan.setUserInfo(userInfo);
                }
            });
        });
        //5.返回PageResult对象
        return new PageResult<>(page.getTotal(), userFanList);
    }

    /**
     * 根据userId获取用户关注数
     */
    @Override
    public Integer getFollowingCountByUserId(Long userId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<UserFollowing>()
                .eq(UserFollowing::getUserId, userId));//用户作为粉丝的记录数是多少
    }

    /**
     * 根据userId获取用户粉丝数
     */
    @Override
    public Integer getFanCountByUserId(Long userId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<UserFollowing>()
                .eq(UserFollowing::getFollowingId, userId));//用户作为被关注的记录数是多少
    }

    /**
     * 初始化用户关注分组
     */
    @Override
    public void initUserFollowingGroup(Long userId) {
        followingGroupService.initUserFollowingGroup(userId);
    }

    /**
     * 根据用户id获取其所有粉丝
     */
    private List<UserFollowing> getUserFans(Long userId) {
        return baseMapper.selectList(new LambdaQueryWrapper<UserFollowing>()
                .eq(UserFollowing::getFollowingId, userId));//userId是被关注的
    }

    /**
     * 根据用户id获取其所有关注
     */
    public List<UserFollowing> getUserFollowings(Long userId) {
        return baseMapper.selectList(new LambdaQueryWrapper<UserFollowing>()
                .eq(UserFollowing::getUserId, userId));
    }
}
