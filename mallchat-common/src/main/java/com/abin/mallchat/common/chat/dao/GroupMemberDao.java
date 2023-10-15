package com.abin.mallchat.common.chat.dao;

import com.abin.mallchat.common.chat.domain.entity.GroupMember;
import com.abin.mallchat.common.chat.domain.enums.GroupRoleEnum;
import com.abin.mallchat.common.chat.mapper.GroupMemberMapper;
import com.abin.mallchat.common.chat.service.cache.GroupMemberCache;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 群成员表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-07-16
 */
@Service
public class GroupMemberDao extends ServiceImpl<GroupMemberMapper, GroupMember> {

    @Autowired
    private GroupMemberCache groupMemberCache;

    public List<Long> getMemberUidList(Long groupId) {
        List<GroupMember> list = lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .select(GroupMember::getUid)
                .list();
        return list.stream().map(GroupMember::getUid).collect(Collectors.toList());
    }

    public List<Long> getMemberBatch(Long groupId, List<Long> uidList) {
        List<GroupMember> list = lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .in(GroupMember::getUid, uidList)
                .select(GroupMember::getUid)
                .list();
        return list.stream().map(GroupMember::getUid).collect(Collectors.toList());
    }

    public GroupMember getMember(Long groupId, Long uid) {
        return lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid, uid)
                .one();
    }

    public List<GroupMember> getSelfGroup(Long uid) {
        return lambdaQuery()
                .eq(GroupMember::getUid, uid)
                .eq(GroupMember::getRole, GroupRoleEnum.LEADER.getType())
                .list();
    }

    /**
     * 判断用户是否在房间中
     *
     * @param roomId  房间ID
     * @param uidList 用户ID
     * @return 是否在群聊中
     */
    public Boolean isGroupShip(Long roomId, List<Long> uidList) {
        List<Long> memberUidList = groupMemberCache.getMemberUidList(roomId);
        return memberUidList.containsAll(uidList);
    }
}
