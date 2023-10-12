package com.ruyi.mallchat.common.user.dao;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruyi.mallchat.common.common.domain.enums.NormalOrNoEnum;
import com.ruyi.mallchat.common.common.domain.vo.request.CursorPageBaseReq;
import com.ruyi.mallchat.common.common.domain.vo.response.CursorPageBaseResp;
import com.ruyi.mallchat.common.common.utils.CursorUtils;
import com.ruyi.mallchat.common.user.domain.entity.User;
import com.ruyi.mallchat.common.user.domain.enums.ChatActiveStatusEnum;
import com.ruyi.mallchat.common.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/djc-github">ruyi</a>
 * @since 2023-10-03
 */
@Service
public class UserDao extends ServiceImpl<UserMapper, User> {
    /**
     * 通过openId查询用户信息
     *
     * @param openId 微信openid用户标识
     * @return 用户信息
     */
    public User getByOpenId(String openId) {
        LambdaQueryWrapper<User> wrapper = new QueryWrapper<User>().lambda().eq(User::getOpenId, openId);
        return getOne(wrapper);
    }

    /**
     * 修改用户名
     *
     * @param uid  唯一标识
     * @param name 新用户名
     */
    public void modifyName(Long uid, String name) {
        User update = new User();
        update.setId(uid);
        update.setName(name);
        updateById(update);
    }

    /**
     * 穿戴新徽章
     *
     * @param uid     唯一标识
     * @param badgeId 佩戴的徽章id
     */
    public void wearingBadge(Long uid, Long badgeId) {
        User update = new User();
        update.setId(uid);
        update.setItemId(badgeId);
        updateById(update);
    }

    /**
     * 通过用户名查询用户信息
     *
     * @param name 用户名
     * @return 用户信息
     */
    public User getByName(String name) {
        return lambdaQuery().eq(User::getName, name).one();
    }

    /**
     * 获取群成员列表
     *
     * @return 最近活跃的1000个用户名和头像信息列表
     */
    public List<User> getMemberList() {
        return lambdaQuery()
                .eq(User::getStatus, NormalOrNoEnum.NORMAL.getStatus())
                .orderByDesc(User::getLastOptTime)//最近活跃的1000个人，可以用lastOptTime字段，但是该字段没索引，updateTime可平替
                .last("limit 1000")//毕竟是大群聊，人数需要做个限制
                .select(User::getId, User::getName, User::getAvatar)
                .list();

    }

    /**
     * 批量获取用户名和头像信息
     *
     * @param uids 用户uid列表
     * @return 用户名和头像信息列表
     */
    public List<User> getFriendList(List<Long> uids) {
        return lambdaQuery()
                .in(User::getId, uids)
                .select(User::getId, User::getActiveStatus, User::getName, User::getAvatar)
                .list();

    }

    /**
     * 获取全部用户上线次数
     *
     * @return 全部用户上线次数
     */
    public Integer getOnlineCount() {
        return getOnlineCount(null);
    }

    /**
     * 获取上线次数
     *
     * @param memberUidList 用户列表
     * @return 列表中用户上线的总次数
     */
    public Integer getOnlineCount(List<Long> memberUidList) {
        return lambdaQuery()
                .eq(User::getActiveStatus, ChatActiveStatusEnum.ONLINE.getStatus())
                .in(CollectionUtil.isNotEmpty(memberUidList), User::getId, memberUidList)
                .count();
    }

    public CursorPageBaseResp<User> getCursorPage(List<Long> memberUidList, CursorPageBaseReq request, ChatActiveStatusEnum online) {
        return CursorUtils.getCursorPageByMysql(this, request, wrapper -> {
            wrapper.eq(User::getActiveStatus, online.getStatus());//筛选上线或者离线的
            wrapper.in(CollectionUtils.isNotEmpty(memberUidList), User::getId, memberUidList);//普通群对uid列表做限制
        }, User::getLastOptTime);
    }

}
