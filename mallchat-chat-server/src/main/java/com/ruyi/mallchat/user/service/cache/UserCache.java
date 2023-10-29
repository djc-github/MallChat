package com.ruyi.mallchat.user.service.cache;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import com.ruyi.mallchat.common.constant.RedisKey;
import com.ruyi.mallchat.common.domain.vo.request.CursorPageBaseReq;
import com.ruyi.mallchat.common.domain.vo.response.CursorPageBaseResp;
import com.ruyi.mallchat.common.utils.CursorUtils;
import com.ruyi.mallchat.common.utils.RedisUtils;
import com.ruyi.mallchat.user.dao.BlackDao;
import com.ruyi.mallchat.user.dao.RoleDao;
import com.ruyi.mallchat.user.dao.UserDao;
import com.ruyi.mallchat.user.dao.UserRoleDao;
import com.ruyi.mallchat.user.domain.entity.Black;
import com.ruyi.mallchat.user.domain.entity.User;
import com.ruyi.mallchat.user.domain.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 用户相关缓存
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-27
 */
@Component
public class UserCache {

    @Autowired
    private UserDao userDao;
    @Autowired
    private BlackDao blackDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private UserSummaryCache userSummaryCache;

    /**
     * 获取在线人数（读redis缓存）
     *
     * @return 在线人数
     */
    public Long getOnlineNum() {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        return RedisUtils.zCard(onlineKey);
    }

    /**
     * 获取不在线人数（读redis缓存）
     *
     * @return 不在线人数
     */
    public Long getOfflineNum() {
        String offlineKey = RedisKey.getKey(RedisKey.OFFLINE_UID_ZET);
        return RedisUtils.zCard(offlineKey);
    }

    /**
     * 从所有(在线和离线)用户列表redis缓存中删除某个用户
     *
     * @param uid 用户id
     */
    public void remove(Long uid) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        String offlineKey = RedisKey.getKey(RedisKey.OFFLINE_UID_ZET);
        //移除离线表
        RedisUtils.zRemove(offlineKey, uid);
        //移除上线表
        RedisUtils.zRemove(onlineKey, uid);
    }

    /**
     * 从离线用户列表redis缓存中删除某用户，并从在线列表redis缓存中增加某用户
     *
     * @param uid     上线用户id
     * @param optTime 上线时间
     */
    public void online(Long uid, Date optTime) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        String offlineKey = RedisKey.getKey(RedisKey.OFFLINE_UID_ZET);
        //移除离线表
        RedisUtils.zRemove(offlineKey, uid);
        //更新上线表
        RedisUtils.zAdd(onlineKey, uid, optTime.getTime());
    }

    /**
     * 从在线列表redis缓存获取所有在线用户列表
     */
    public List<Long> getOnlineUidList() {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        Set<String> strings = RedisUtils.zAll(onlineKey);
        return strings.stream().map(Long::parseLong).collect(Collectors.toList());
    }

    /**
     * 判断在线列表redis缓存中是否存在某用户
     *
     * @param uid 用户id
     * @return 用户是否在线
     */
    public boolean isOnline(Long uid) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        return RedisUtils.zIsMember(onlineKey, uid);
    }

    /**
     * 从离线用户列表redis缓存中增加某用户，并从在线列表redis缓存中删除某用户
     *
     * @param uid     离线用户id
     * @param optTime 离线时间
     */
    public void offline(Long uid, Date optTime) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        String offlineKey = RedisKey.getKey(RedisKey.OFFLINE_UID_ZET);
        //移除上线线表
        RedisUtils.zRemove(onlineKey, uid);
        //更新离线表
        RedisUtils.zAdd(offlineKey, uid, optTime.getTime());
    }

    public CursorPageBaseResp<Pair<Long, Double>> getOnlineCursorPage(CursorPageBaseReq pageBaseReq) {
        return CursorUtils.getCursorPageByRedis(pageBaseReq, RedisKey.getKey(RedisKey.ONLINE_UID_ZET), Long::parseLong);
    }

    public CursorPageBaseResp<Pair<Long, Double>> getOfflineCursorPage(CursorPageBaseReq pageBaseReq) {
        return CursorUtils.getCursorPageByRedis(pageBaseReq, RedisKey.getKey(RedisKey.OFFLINE_UID_ZET), Long::parseLong);
    }

    /**
     * 从redis缓存获取用户信息最后一次修改时间
     *
     * @param uidList 多个用户id
     * @return 多个修改时间，没有的值是null
     */
    public List<Long> getUserModifyTime(List<Long> uidList) {
        //组装key
        List<String> keys = uidList.stream().map(uid -> RedisKey.getKey(RedisKey.USER_MODIFY_STRING, uid)).collect(Collectors.toList());
        return RedisUtils.mget(keys, Long.class);
    }

    /**
     * 刷新用户信息更新时间 的redis缓存，不读数据库了，直接使用当前时间
     *
     * @param uid 用户id
     */
    public void refreshUserModifyTime(Long uid) {
        String key = RedisKey.getKey(RedisKey.USER_MODIFY_STRING, uid);
        RedisUtils.set(key, new Date().getTime());
    }

    /**
     * 获取用户信息，盘路缓存模式
     */
    public User getUserInfo(Long uid) {//todo 后期做二级缓存
        return getUserInfoBatch(Collections.singleton(uid)).get(uid);
    }

    /**
     * 获取用户信息，盘路缓存模式
     */
    public Map<Long, User> getUserInfoBatch(Set<Long> uids) {
        //批量组装key
        List<String> keys = uids.stream().map(a -> RedisKey.getKey(RedisKey.USER_INFO_STRING, a)).collect(Collectors.toList());
        //批量get用户信息
        List<User> mget = RedisUtils.mget(keys, User.class);
        Map<Long, User> cachedUserInfos = mget.stream().filter(Objects::nonNull).collect(Collectors.toMap(User::getId, Function.identity()));
        //获取差集——缓存中没有，需要load更新的uid
        List<Long> needLoadUidList = uids.stream().filter(a -> !cachedUserInfos.containsKey(a)).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(needLoadUidList)) {
            //批量load
            List<User> needLoadUserList = userDao.listByIds(needLoadUidList);
            Map<String, User> redisMap = needLoadUserList.stream().collect(Collectors.toMap(a -> RedisKey.getKey(RedisKey.USER_INFO_STRING, a.getId()), Function.identity()));
            RedisUtils.mset(redisMap, 5 * 60);
            //更新要返回的用户信息集合
            cachedUserInfos.putAll(needLoadUserList.stream().collect(Collectors.toMap(User::getId, Function.identity())));
        }
        return cachedUserInfos;
    }

    /**
     * 清空用户信息所有相关缓存
     *
     * @param uid 用户id
     */
    public void userInfoChange(Long uid) {
        //删除用户基本信息redis缓存
        delUserInfo(uid);
        //删除用户相关缓存的集合缓存，前端下次懒加载的时候可以获取到最新的数据
        userSummaryCache.delete(uid);
        //刷新用户信息修改时间的redis缓存
        refreshUserModifyTime(uid);
    }

    /**
     * 删除用户redis缓存信息
     *
     * @param uid 用户id
     */
    public void delUserInfo(Long uid) {
        String key = RedisKey.getKey(RedisKey.USER_INFO_STRING, uid);
        RedisUtils.del(key);
    }

    /**
     * 使用本地缓存，查询黑名单并按拉黑目标类型分组
     *
     * @return 黑名单map，key为按拉黑目标类型
     */
    @Cacheable(cacheNames = "user", key = "'blackList'")
    public Map<Integer, Set<String>> getBlackMap() {
        Map<Integer, List<Black>> collect = blackDao.list().stream().collect(Collectors.groupingBy(Black::getType));
        Map<Integer, Set<String>> result = new HashMap<>(collect.size());
        for (Map.Entry<Integer, List<Black>> entry : collect.entrySet()) {
            result.put(entry.getKey(), entry.getValue().stream().map(Black::getTarget).collect(Collectors.toSet()));
        }
        return result;
    }

    /**
     * 清除本地黑名单缓存
     *
     * @return
     */
    @CacheEvict(cacheNames = "user", key = "'blackList'")
    public Map<Integer, Set<String>> evictBlackMap() {
        return null;
    }

    /**
     * 使用本地缓存，查询用户所有角色
     *
     * @param uid 用户id
     * @return 用户拥有的角色集合
     */
    @Cacheable(cacheNames = "user", key = "'roles'+#uid")
    public Set<Long> getRoleSet(Long uid) {
        List<UserRole> userRoles = userRoleDao.listByUid(uid);
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
    }
}
