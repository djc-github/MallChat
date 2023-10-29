package com.ruyi.mallchat.user.service.impl;

import com.ruyi.mallchat.user.domain.enums.RoleEnum;
import com.ruyi.mallchat.user.service.RoleService;
import com.ruyi.mallchat.user.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

/**
 * Description: 角色管理
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-04
 */
@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private UserCache userCache;

    /**
     * 校验用户是否有该权限
     *
     * @param uid      用户id
     * @param roleEnum 要校验的权限
     * @return 是否有权限
     */
    @Override
    public boolean hasPower(Long uid, RoleEnum roleEnum) {//todo 超级管理员无敌的好吧，后期做成权限=》资源模式
        Set<Long> roleSet = userCache.getRoleSet(uid);
        return isAdmin(roleSet) || roleSet.contains(roleEnum.getId());
    }

    /**
     * 是否有超级管理员角色
     *
     * @param roleSet 角色列表
     * @return 是否有超级管理员
     */
    private boolean isAdmin(Set<Long> roleSet) {
        return Objects.requireNonNull(roleSet).contains(RoleEnum.ADMIN.getId());
    }
}
