package com.wz.pilipili.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.wz.pilipili.constant.UserConstant;
import com.wz.pilipili.entity.auth.UserRole;
import com.wz.pilipili.entity.page.PageResult;
import com.wz.pilipili.entity.user.RefreshTokenDetail;
import com.wz.pilipili.entity.user.User;
import com.wz.pilipili.entity.user.UserInfo;
import com.wz.pilipili.exception.ConditionException;
import com.wz.pilipili.search.client.SearchFeignClient;
import com.wz.pilipili.user.mapper.UserMapper;
import com.wz.pilipili.user.service.*;
import com.wz.pilipili.util.MD5Util;
import com.wz.pilipili.util.RSAUtil;
import com.wz.pilipili.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 用户表 服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private SearchFeignClient searchFeignClient;

    /**
     * 注册
     */
    @Override
    @Transactional
    public void register(User user) {
        //1.获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("手机号不能为空");
        }
        //2.通过手机号查询，看是否已经注册
        User dbUser = this.getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (dbUser != null) {
            throw new ConditionException("该手机号已经注册");
        }
        //3.对用户密码（此时的密码是经过RSA加密过后的）使用RSA密钥解密；
        //  通过时间戳 => 盐值，然后利用MD5对用户密码进行加密
        Date now = new Date();
        String salt = String.valueOf(now.getTime());//盐值
        String password = user.getPassword();
        String rawPassword;//原始密码
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        //4.对User对象进行赋值：salt，password，然后插入数据库
        user.setSalt(salt);
        user.setPassword(md5Password);
        baseMapper.insert(user);
        //5.将用户user的id拿出来 => userId，然后创建相关的用户信息，插入用户信息表t_user_info
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setNick(UserConstant.DEFAULT_NICK);
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.GENDER_MALE);
        userInfoService.save(userInfo);
        //6.添加UserInfo到ES
        searchFeignClient.addUserInfo(userInfo);
        //7.添加用户默认权限角色
        userAuthService.addUserDefaultRole(user.getId());
    }

    /**
     * 登录
     */
    @Override
    public String login(User user) throws Exception {
        //1.判断手机号是否为空
        String phone = user.getPhone() == null ? "" : user.getPhone();
        String email = user.getEmail() == null ? "" : user.getEmail();
        if (StringUtils.isNullOrEmpty(phone) && StringUtils.isNullOrEmpty(email)) {
            throw new ConditionException("参数异常");
        }
        //2.通过手机号/邮箱查询，看是否以及存在
        User dbUser = this.getUserByPhoneOrEmail(phone, email);
        if (dbUser == null) {
            throw new ConditionException("当前用户不存在");
        }
        //3.获取password（前端RSA加密过后的密码），然后RSA解密；
        //  获取盐值salt，用MD5进行加密，然后使用密文和数据库中的进行比对
        String password = user.getPassword();
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }
        String salt = dbUser.getSalt();//注意是dbUser
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");//密文
        if (!md5Password.equals(dbUser.getPassword())) {
            throw new ConditionException("密码错误！");
        }
        //4.生成用户令牌返回前端（包括用户id和用户角色信息）
        Long userId = dbUser.getId();
        List<UserRole> roleList = userRoleService.getUserRoleListByUserId(userId);
        return TokenUtil.generateToken(userId, roleList);
    }

    /**
     * 登录，并返回双令牌：accessToken和refreshToken
     */
    @Transactional
    @Override
    public Map<String, Object> loginForDts(User user) throws Exception {
        //1.判断手机号是否为空
        String phone = user.getPhone() == null ? "" : user.getPhone();
        String email = user.getEmail() == null ? "" : user.getEmail();
        if (StringUtils.isNullOrEmpty(phone) && StringUtils.isNullOrEmpty(email)) {
            throw new ConditionException("参数异常");
        }
        //2.通过手机号/邮箱查询，看是否以及存在
        User dbUser = this.getUserByPhoneOrEmail(phone, email);
        if (dbUser == null) {
            throw new ConditionException("当前用户不存在");
        }
        //3.获取password（前端RSA加密过后的密码），然后RSA解密；
        //  获取盐值salt，用MD5进行加密，然后使用密文和数据库中的进行比对
        String password = user.getPassword();
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }
        String salt = dbUser.getSalt();//注意是dbUser
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");//密文
        if (!md5Password.equals(dbUser.getPassword())) {
            throw new ConditionException("密码错误！");
        }
        //4.生成accessToken和refreshToken
        Long userId = dbUser.getId();
        List<UserRole> roleList = userRoleService.getUserRoleListByUserId(userId);
        String accessToken = TokenUtil.generateToken(userId, roleList);
        String refreshToken = TokenUtil.generateRefreshToken(userId);
        //5.将refreshToken和userId保存到数据库中
        //为了后续，用户退出登录或者想延迟刷新accessToken的有效期时，
        //去查找跟userId相关的refreshToken，如果存在，说明refreshToken仍在有效期中，那么accessToken就可以被刷新
        //否则，refreshToken失效了，那么就需要告诉前端，token失效，需要重新登录

        //这里先删除后添加，就满足了更新的业务逻辑
        refreshTokenService.deleteRefreshTokenByUserId(userId);
        refreshTokenService.addRefreshToken(userId, refreshToken);
        //6.构建返回参数
        Map<String, Object> map = new HashMap<>();
        map.put("accessToken", accessToken);
        map.put("refreshToken", refreshToken);
        return map;
    }

    /**
     * 用户登出
     */
    @Override
    public void logout(Long userId) {
        refreshTokenService.deleteRefreshTokenByUserId(userId);
    }

    /**
     * 刷新accessToken
     */
    @Override
    public String refreshAccessToken(String refreshToken) throws Exception {
        RefreshTokenDetail refreshTokenDetail = refreshTokenService.getRefreshToken(refreshToken);
        if (refreshTokenDetail == null) {
            throw new ConditionException("555", "token过期！");
        }
        Long userId = refreshTokenDetail.getUserId();
        List<UserRole> roleList = userRoleService.getUserRoleListByUserId(userId);
        return TokenUtil.generateToken(userId, roleList);
    }

    /**
     * 根据userId集合批量获取用户信息列表
     */
    @Override
    public List<UserInfo> batchGetUserInfoListByUserIds(Set<Long> userIds) {
        return userInfoService.getUserInfoListByUserIds(userIds);
    }

    /**
     * 根据userId获取用户信息
     */
    @Override
    public UserInfo getUserInfoByUserId(Long userId) {
        return userInfoService.getUserInfoByUserId(userId);
    }


    /**
     * 根据userId查询用户
     */
    @Override
    public User getUserInfo(Long userId) {
        User user = this.getById(userId);
        //还需要设置UserInfo
        UserInfo userInfo = userInfoService.getUserInfoByUserId(userId);
        user.setUserInfo(userInfo);
        return user;
    }

    /**
     * 更新用户
     */
    @Override
    public void updateUser(User user) throws Exception {
        //1.根据userId查询用户是否存在
        User dbUser = this.getById(user.getId());
        if (dbUser == null) {
            throw new ConditionException("用户不存在");
        }
        //2.如果传递的user对象密码不为空，更新数据库的密码为 当前密码
        String password = user.getPassword();
        if (!StringUtils.isNullOrEmpty(password)) {
            String rawPassword = RSAUtil.decrypt(password);
            String md5Password = MD5Util.sign(rawPassword, user.getSalt(), "UTF-8");
            user.setPassword(md5Password);
        }
        //3.数据库对应行记录更新
        baseMapper.updateById(user);
    }

    /**
     * 更新用户信息
     */
    @Override
    public void updateUserInfo(UserInfo userInfo) {
        userInfoService.update(userInfo, new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUserId, userInfo.getUserId()));
    }

    /**
     * 分页获取用户信息列表
     */
    @Override
    public PageResult<UserInfo> pageListUserInfos(JSONObject params) {
        //1.通过params获取对应的参数
        Integer no = params.getInteger("no");//当前页：no
        Integer size = params.getInteger("size");//每页显示size条
        //2.根据参数 查询 数据库中查询对应范围的数据
        //  start: 从数据库第start[从0开始]条数据开始查。no = 2，size = 5，那么就从第5条数据开始查
        //  limit: 每次查limit条数据。这个跟size保持一致就好了
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        //3.通过params 查询需要的总记录数total 和 对应的分页数据List<UserInfo>
        IPage<UserInfo> ipage = userInfoService.pageListUserInfos(params);
        return new PageResult<>(ipage.getTotal(), ipage.getRecords());
    }


    /**
     * 通过phone或者email获取用户
     */
    private User getUserByPhoneOrEmail(String phone, String email) {
        return this.getOne(new LambdaQueryWrapper<User>().eq(User::getPhone,
                phone).or().eq(User::getEmail, email));
    }

}
