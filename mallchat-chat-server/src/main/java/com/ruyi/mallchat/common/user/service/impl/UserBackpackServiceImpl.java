package com.ruyi.mallchat.common.user.service.impl;

import com.ruyi.mallchat.common.common.annotation.RedissonLock;
import com.ruyi.mallchat.common.common.domain.enums.IdempotentEnum;
import com.ruyi.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.ruyi.mallchat.common.common.event.ItemReceiveEvent;
import com.ruyi.mallchat.common.user.dao.UserBackpackDao;
import com.ruyi.mallchat.common.user.domain.entity.ItemConfig;
import com.ruyi.mallchat.common.user.domain.entity.UserBackpack;
import com.ruyi.mallchat.common.user.domain.enums.ItemTypeEnum;
import com.ruyi.mallchat.common.user.service.UserBackpackService;
import com.ruyi.mallchat.common.user.service.cache.ItemCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 用户背包表 服务类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-19
 */
@Service
public class UserBackpackServiceImpl implements UserBackpackService {
    @Autowired
    private UserBackpackDao userBackpackDao;
    @Autowired
    private ItemCache itemCache;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    @Lazy
    private UserBackpackServiceImpl userBackpackService;

    @Override
    public void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        //组装幂等号
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);
        //发放物品
        userBackpackService.doAcquireItem(uid, itemId, idempotent);
    }

    /**
     * 给用户发放物品
     *
     * @param uid        用户id
     * @param itemId     物品id
     * @param idempotent 幂等号
     */
    @RedissonLock(key = "#idempotent", waitTime = 5000)//相同幂等如果同时发奖，需要排队等上一个执行完，取出之前数据返回
    public void doAcquireItem(Long uid, Long itemId, String idempotent) {
        UserBackpack userBackpack = userBackpackDao.getByIdp(idempotent);
        //幂等检查
        if (Objects.nonNull(userBackpack)) {
            return;
        }
        //业务检查
        ItemConfig itemConfig = itemCache.getById(itemId);
        if (ItemTypeEnum.BADGE.getType().equals(itemConfig.getType())) {//对徽章类型物品做唯一性检查,只能有一个
            Integer countByValidItemId = userBackpackDao.getCountByValidItemId(uid, itemId);
            if (countByValidItemId > 0) {//已经有徽章了不发
                return;
            }
        }
        //发物品
        UserBackpack insert = UserBackpack.builder()
                .uid(uid)
                .itemId(itemId)
                .status(YesOrNoEnum.NO.getStatus())
                .idempotent(idempotent)
                .build();
        userBackpackDao.save(insert);
        //发布用户收到物品的事件
        applicationEventPublisher.publishEvent(new ItemReceiveEvent(this, insert));
    }

    /**
     * 组装幂等号
     *
     * @param itemId         物品id
     * @param idempotentEnum 幂等类型
     * @param businessId     上层业务发送的唯一标识
     * @return 幂等号
     */
    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d_%d_%s", itemId, idempotentEnum.getType(), businessId);
    }
}
