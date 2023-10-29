package com.ruyi.mallchat.common.event.listener;


import com.ruyi.mallchat.chat.dao.GroupMemberDao;
import com.ruyi.mallchat.chat.domain.entity.GroupMember;
import com.ruyi.mallchat.chat.domain.entity.RoomGroup;
import com.ruyi.mallchat.chat.domain.vo.request.ChatMessageReq;
import com.ruyi.mallchat.chat.service.ChatService;
import com.ruyi.mallchat.chat.service.adapter.MemberAdapter;
import com.ruyi.mallchat.chat.service.adapter.RoomAdapter;
import com.ruyi.mallchat.chat.service.cache.GroupMemberCache;
import com.ruyi.mallchat.chat.service.cache.MsgCache;
import com.ruyi.mallchat.common.event.GroupMemberAddEvent;
import com.ruyi.mallchat.user.dao.UserDao;
import com.ruyi.mallchat.user.domain.entity.User;
import com.ruyi.mallchat.user.producer.PushProducer;
import com.ruyi.mallchat.user.service.cache.UserInfoCache;
import com.ruyi.mallchat.websocket.domain.vo.response.WSBaseResp;
import com.ruyi.mallchat.websocket.domain.vo.response.WSMemberChange;
import com.ruyi.mallchat.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 添加群成员监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class GroupMemberAddListener {
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private MsgCache msgCache;
    @Autowired
    private UserInfoCache userInfoCache;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private GroupMemberDao groupMemberDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private GroupMemberCache groupMemberCache;
    @Autowired
    private PushProducer pushProducer;


    @Async
    @TransactionalEventListener(classes = GroupMemberAddEvent.class, fallbackExecution = true)
    public void sendAddMsg(GroupMemberAddEvent event) {
        List<GroupMember> memberList = event.getMemberList();
        RoomGroup roomGroup = event.getRoomGroup();
        Long inviteUid = event.getInviteUid();
        User user = userInfoCache.get(inviteUid);
        List<Long> uidList = memberList.stream().map(GroupMember::getUid).collect(Collectors.toList());
        ChatMessageReq chatMessageReq = RoomAdapter.buildGroupAddMessage(roomGroup, user, userInfoCache.getBatch(uidList));
        chatService.sendMsg(chatMessageReq, User.UID_SYSTEM);
    }

    @Async
    @TransactionalEventListener(classes = GroupMemberAddEvent.class, fallbackExecution = true)
    public void sendChangePush(GroupMemberAddEvent event) {
        List<GroupMember> memberList = event.getMemberList();
        RoomGroup roomGroup = event.getRoomGroup();
        List<Long> memberUidList = groupMemberCache.getMemberUidList(roomGroup.getRoomId());
        List<Long> uidList = memberList.stream().map(GroupMember::getUid).collect(Collectors.toList());
        List<User> users = userDao.listByIds(uidList);
        users.forEach(user -> {
            WSBaseResp<WSMemberChange> ws = MemberAdapter.buildMemberAddWS(roomGroup.getRoomId(), user);
            pushProducer.sendPushMsg(ws, memberUidList);
        });
        //移除缓存
        groupMemberCache.evictMemberUidList(roomGroup.getRoomId());
    }

}
