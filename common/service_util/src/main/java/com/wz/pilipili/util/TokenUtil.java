package com.wz.pilipili.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.wz.pilipili.entity.auth.UserRole;
import com.wz.pilipili.exception.ConditionException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 基于JWT生成token
 */
public class TokenUtil {

    private static final String ISSUER = "签发者";

    /**
     * 生成token
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public static String generateToken(Long userId, List<UserRole> roleList) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());//加密算法
        Calendar calendar = Calendar.getInstance();//日历类，为了后续生成jwt的过期时间
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 2);

        String rolesJson = JSON.toJSONString(roleList);

        return JWT.create().withKeyId(String.valueOf(userId)) //使用用户id
                .withIssuer(ISSUER)                          //jtw的签发者（个人或者机构都可以）
                .withExpiresAt(calendar.getTime())          //设置过期时间
                .withClaim("roles", rolesJson)            //设置角色信息
                .sign(algorithm);                           //设置签名算法
    }


    /**
     * 验证token
     *
     * @param token
     * @return
     */
    public static Long verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());//解密算法
            JWTVerifier verifier = JWT.require(algorithm).build();//构建JWT验证类
            DecodedJWT jwt = verifier.verify(token);//验证，返回解密后的jwt
            String userId = jwt.getKeyId();//获取userId
            return Long.valueOf(userId);
        } catch (TokenExpiredException e) {//用户令牌token过期，设置错误码555，前端可以根据错误码进行处理：刷新token
            throw new ConditionException("555", "token过期！");
        } catch (Exception e) {
            throw new ConditionException("非法用户token！");
        }
    }

    /**
     * 获取token中的角色信息
     */
    public static String getRolesFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("roles").asString();
        } catch (Exception e) {
            throw new ConditionException("无法解析用户角色信息！");
        }
    }

    /**
     * 生成refreshToken
     */
    public static String generateRefreshToken(Long userId) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }

    public static void verifyRefreshToken(String refreshToken) {
        TokenUtil.verifyToken(refreshToken);
    }
}
