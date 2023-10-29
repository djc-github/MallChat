package com.ruyi.mallchat.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruyi.mallchat.common.domain.enums.YesOrNoEnum;
import com.ruyi.mallchat.user.domain.entity.UserBackpack;
import com.ruyi.mallchat.user.mapper.UserBackpackMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户背包表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-19
 */
@Service
public class UserBackpackDao extends ServiceImpl<UserBackpackMapper, UserBackpack> {

    /**
     * 查询用户某物品可用数量
     *
     * @param uid    用户id
     * @param itemId 物品id
     * @return 剩余可用数量
     */
    public Integer getCountByValidItemId(Long uid, Long itemId) {
        return lambdaQuery().eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .count();
    }

    /**
     * 通过id查询用户背包中物品，只返回查到的第一个
     *
     * @param uid    用户id
     * @param itemId 物品id
     * @return 背包中对应物品信息
     */
    public UserBackpack getFirstValidItem(Long uid, Long itemId) {
        LambdaQueryWrapper<UserBackpack> wrapper = new QueryWrapper<UserBackpack>().lambda()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .last("limit 1");
        return getOne(wrapper);
    }

    /**
     * 使背包物品失效（使用物品）
     *
     * @param id 背包物品id
     * @return 使用成功 or 失败
     */
    public boolean invalidItem(Long id) {
        UserBackpack update = new UserBackpack();
        update.setId(id);
        update.setStatus(YesOrNoEnum.YES.getStatus());
        return updateById(update);
    }

    /**
     * 通过物品id查询用户背包中所有该物品信息
     *
     * @param uid     用户id
     * @param itemIds 多个物品id
     * @return 多个背包物品
     */
    public List<UserBackpack> getByItemIds(Long uid, List<Long> itemIds) {
        return lambdaQuery().eq(UserBackpack::getUid, uid)
                .in(UserBackpack::getItemId, itemIds)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .list();
    }

    /**
     * 通过物品id查询多个用户背包中所有该物品
     *
     * @param uids    多个用户id
     * @param itemIds 多个物品id
     * @return 多个背包物品
     */
    public List<UserBackpack> getByItemIds(List<Long> uids, List<Long> itemIds) {
        return lambdaQuery().in(UserBackpack::getUid, uids)
                .in(UserBackpack::getItemId, itemIds)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .list();
    }

    /**
     * 通过幂等号查询对应背包物品信息
     *
     * @param idempotent 背包物品幂等号
     * @return 对应背包物品
     */
    public UserBackpack getByIdp(String idempotent) {
        return lambdaQuery().eq(UserBackpack::getIdempotent, idempotent).one();
    }
}
