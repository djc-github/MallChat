package com.ruyi.mallchat.common.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.ruyi.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.ruyi.mallchat.common.user.domain.entity.ItemConfig;
import com.ruyi.mallchat.common.user.domain.entity.User;
import com.ruyi.mallchat.common.user.domain.entity.UserBackpack;
import com.ruyi.mallchat.common.user.domain.vo.response.user.BadgeResp;
import com.ruyi.mallchat.common.user.domain.vo.response.user.UserInfoResp;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description: 用户适配器
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Slf4j
public class UserAdapter {
    /**
     * 用户名最大长度
     */
    public static final int MAX_NAME_LENGTH = 6;

    /**
     * 构建用户信息
     *
     * @param openId 用户openid
     * @return 用户信息
     */
    public static User buildUser(String openId) {
        User user = new User();
        user.setOpenId(openId);
        return user;
    }

    public static User buildAuthorizeUser(Long id, WxOAuth2UserInfo userInfo) {
        User user = new User();
        user.setId(id);
        user.setAvatar(userInfo.getHeadImgUrl());
        user.setSex(userInfo.getSex());
//        user.setName(userInfo.getNickname());
        String nickname = userInfo.getNickname();
        if (nickname.length() > MAX_NAME_LENGTH) {
            user.setName("名字过长" + RandomUtil.randomInt(100000));
        } else {
            user.setName(nickname);
        }
        return user;
    }

    public static UserInfoResp buildUserInfoResp(User userInfo, Integer countByValidItemId) {
        UserInfoResp userInfoResp = new UserInfoResp();
        BeanUtil.copyProperties(userInfo, userInfoResp);
        userInfoResp.setModifyNameChance(countByValidItemId);
        return userInfoResp;
    }

    /**
     * 构建用户物品(徽章)列表
     *
     * @param itemConfigs 物品信息
     * @param backpacks   用户背包物品列表
     * @param user        用户
     * @return
     */
    public static List<BadgeResp> buildBadgeResp(List<ItemConfig> itemConfigs, List<UserBackpack> backpacks, User user) {
        if (ObjectUtil.isNull(user)) {
            // 这里 user 入参可能为空，防止 NPE 问题
            return Collections.emptyList();
        }

        Set<Long> obtainItemSet = backpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toSet());
        return itemConfigs.stream().map(a -> {
                    BadgeResp resp = new BadgeResp();
                    BeanUtil.copyProperties(a, resp);
                    resp.setObtain(obtainItemSet.contains(a.getId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
                    resp.setWearing(ObjectUtil.equal(a.getId(), user.getItemId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
                    return resp;
                }).sorted(Comparator.comparing(BadgeResp::getWearing, Comparator.reverseOrder())
                        .thenComparing(BadgeResp::getObtain, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}
