package com.ruyi.mallchat.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.ruyi.mallchat.common.constant.RedisKey;
import com.ruyi.mallchat.common.utils.JwtUtils;
import com.ruyi.mallchat.common.utils.RedisUtils;
import com.ruyi.mallchat.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Description: 登录相关处理类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    //token过期时间
    private static final Integer TOKEN_EXPIRE_DAYS = 5;
    //token续期时间
    private static final Integer TOKEN_RENEWAL_DAYS = 2;
    @Resource
    private JwtUtils jwtUtils;

    /**
     * 校验token是否在有效期
     *
     * @param oldToken
     * @param uid
     * @return
     */
    private static boolean isTokenNotExpired(String oldToken, Long uid) {
        String key = RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
        String realToken = RedisUtils.getStr(key);
        return Objects.equals(oldToken, realToken);
    }

    /**
     * 校验token是不是有效
     *
     * @param token 令牌
     * @return token有效性
     */
    @Override
    public boolean verify(String token) {
        // 解析token并获取uid
        Long uid = jwtUtils.getUidOrNull(token);
        if (Objects.isNull(uid)) {
            return false;
        }
        //因为这是前端传来的token，有可能token失效了，需要校验是不是和最新token一致
        return isTokenNotExpired(token, uid);
    }

    /**
     * 如果token存在且过期时间小于{@value TOKEN_RENEWAL_DAYS}，续期令牌
     *
     * @param token 令牌
     */
    @Async
    @Override
    public void renewalTokenIfNecessary(String token) {
        // 解析token并获取uid
        Long uid = jwtUtils.getUidOrNull(token);
        if (Objects.isNull(uid)) {
            return;
        }
        //获取token过期时间
        String key = RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
        long expireDays = RedisUtils.getExpire(key, TimeUnit.DAYS);
        if (expireDays == -2) {//不存在的key
            return;
        }
        //小于一天的token帮忙续期
        if (expireDays < TOKEN_RENEWAL_DAYS) {
            RedisUtils.expire(key, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        }
    }

    /**
     * 获取用户token或者颁发新token
     *
     * @param uid 用户id
     * @return token
     */
    @Override
    public String login(Long uid) {
        String key = RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
        String token = RedisUtils.getStr(key);
        if (StrUtil.isNotBlank(token)) {
            return token;
        }
        //获取用户token失败，颁发新token
        token = jwtUtils.createToken(uid);
        //TODO: token过期用redis中心化控制，初期采用5天过期，剩1天自动续期的方案。后续可以用双token实现
        RedisUtils.set(key, token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        return token;
    }

    /**
     * 验证token并获取uid
     *
     * @param token 令牌
     * @return uid or null
     */
    @Override
    public Long getValidUid(String token) {
        boolean verify = verify(token);
        return verify ? jwtUtils.getUidOrNull(token) : null;
    }

}
