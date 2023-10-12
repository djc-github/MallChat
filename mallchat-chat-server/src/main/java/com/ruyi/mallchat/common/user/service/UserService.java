package com.ruyi.mallchat.common.user.service;

import com.ruyi.mallchat.common.user.domain.dto.ItemInfoDTO;
import com.ruyi.mallchat.common.user.domain.dto.SummeryInfoDTO;
import com.ruyi.mallchat.common.user.domain.entity.User;
import com.ruyi.mallchat.common.user.domain.vo.request.user.*;
import com.ruyi.mallchat.common.user.domain.vo.response.user.BadgeResp;
import com.ruyi.mallchat.common.user.domain.vo.response.user.UserInfoResp;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author <a href="https://github.com/djc-github">ruyi</a>
 * @since 2023-10-03
 */
public interface UserService {

    /**
     * 获取前端展示信息
     *
     * @param uid
     * @return
     */
    UserInfoResp getUserInfo(Long uid);

    /**
     * 修改用户名
     *
     * @param uid
     * @param req
     */
    void modifyName(Long uid, ModifyNameReq req);

    /**
     * 获取用户徽章列表
     *
     * @param uid
     */
    List<BadgeResp> badges(Long uid);

    /**
     * 佩戴徽章
     *
     * @param uid
     * @param req
     */
    void wearingBadge(Long uid, WearingBadgeReq req);

    /**
     * 用户注册，需要获得id
     *
     * @param user 用户信息
     */
    void register(User user);

    void black(BlackReq req);

    /**
     * 获取用户汇总信息
     *
     * @param req
     * @return
     */
    List<SummeryInfoDTO> getSummeryUserInfo(SummeryInfoReq req);

    List<ItemInfoDTO> getItemInfo(ItemInfoReq req);
}
