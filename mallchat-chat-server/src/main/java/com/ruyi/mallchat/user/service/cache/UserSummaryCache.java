package com.ruyi.mallchat.user.service.cache;

import com.ruyi.mallchat.common.constant.RedisKey;
import com.ruyi.mallchat.common.service.cache.AbstractRedisStringCache;
import com.ruyi.mallchat.user.dao.UserBackpackDao;
import com.ruyi.mallchat.user.domain.dto.SummeryInfoDTO;
import com.ruyi.mallchat.user.domain.entity.*;
import com.ruyi.mallchat.user.domain.enums.ItemTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 用户详细信息缓存 （只组装所有用户相关缓存，不保证数据是最新的）
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-10
 */
@Component
public class UserSummaryCache extends AbstractRedisStringCache<Long, SummeryInfoDTO> {
    @Autowired
    private UserInfoCache userInfoCache;
    @Autowired
    private UserBackpackDao userBackpackDao;
    @Autowired
    private ItemCache itemCache;

    @Override
    protected String getKey(Long uid) {
        return RedisKey.getKey(RedisKey.USER_SUMMARY_STRING, uid);
    }

    @Override
    protected Long getExpireSeconds() {
        return 10 * 60L;
    }

    @Override
    protected Map<Long, SummeryInfoDTO> load(List<Long> uidList) {//todo 后续可优化徽章信息也异步加载
        //用户基本信息
        Map<Long, User> userMap = userInfoCache.getBatch(uidList);
        //用户徽章信息
        List<ItemConfig> itemConfigs = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        List<Long> itemIds = itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList());
        List<UserBackpack> backpacks = userBackpackDao.getByItemIds(uidList, itemIds);
        Map<Long, List<UserBackpack>> userBadgeMap = backpacks.stream().collect(Collectors.groupingBy(UserBackpack::getUid));
        //todo 需不需要加上：查询用户最后一次更新时间 判断是否需要刷新

        //组装summeryInfoDTO
        return uidList.stream().map(uid -> {
                    SummeryInfoDTO summeryInfoDTO = new SummeryInfoDTO();
                    User user = userMap.get(uid);
                    if (Objects.isNull(user)) {
                        return null;
                    }
                    List<UserBackpack> userBackpacks = userBadgeMap.getOrDefault(user.getId(), new ArrayList<>());
                    summeryInfoDTO.setUid(user.getId());
                    summeryInfoDTO.setName(user.getName());
                    summeryInfoDTO.setAvatar(user.getAvatar());
                    summeryInfoDTO.setLocPlace(Optional.ofNullable(user.getIpInfo()).map(IpInfo::getUpdateIpDetail).map(IpDetail::getCity).orElse(null));
                    summeryInfoDTO.setWearingItemId(user.getItemId());
                    summeryInfoDTO.setItemIds(userBackpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toList()));
                    return summeryInfoDTO;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SummeryInfoDTO::getUid, Function.identity()));
    }
}