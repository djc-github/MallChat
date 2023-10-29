package com.ruyi.mallchat.chat.consumer;

import com.ruyi.mallchat.chat.dao.ContactDao;
import com.ruyi.mallchat.chat.dao.MessageDao;
import com.ruyi.mallchat.chat.dao.RoomDao;
import com.ruyi.mallchat.chat.dao.RoomFriendDao;
import com.ruyi.mallchat.chat.domain.entity.Message;
import com.ruyi.mallchat.chat.domain.entity.Room;
import com.ruyi.mallchat.chat.domain.entity.RoomFriend;
import com.ruyi.mallchat.chat.domain.enums.RoomTypeEnum;
import com.ruyi.mallchat.chat.domain.vo.response.ChatMessageResp;
import com.ruyi.mallchat.chat.service.ChatService;
import com.ruyi.mallchat.chat.service.WeChatMsgOperationService;
import com.ruyi.mallchat.chat.service.cache.GroupMemberCache;
import com.ruyi.mallchat.chat.service.cache.HotRoomCache;
import com.ruyi.mallchat.chat.service.cache.RoomCache;
import com.ruyi.mallchat.chatai.service.ChatAIService;
import com.ruyi.mallchat.common.constant.MQConstant;
import com.ruyi.mallchat.common.domain.dto.MsgSendMessageDTO;
import com.ruyi.mallchat.user.producer.PushProducer;
import com.ruyi.mallchat.user.service.cache.UserCache;
import com.ruyi.mallchat.websocket.service.WebSocketService;
import com.ruyi.mallchat.websocket.service.adapter.WSAdapter;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Description: 发送消息更新房间收信箱，并同步给房间成员信箱
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-08-12
 */
@RocketMQMessageListener(consumerGroup = MQConstant.SEND_MSG_GROUP, topic = MQConstant.SEND_MSG_TOPIC)
@Component
public class MsgSendConsumer implements RocketMQListener<MsgSendMessageDTO> {
    @Autowired
    WeChatMsgOperationService weChatMsgOperationService;
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private ChatAIService openAIService;
    @Autowired
    private RoomCache roomCache;
    @Autowired
    private RoomDao roomDao;
    @Autowired
    private GroupMemberCache groupMemberCache;
    @Autowired
    private UserCache userCache;
    @Autowired
    private RoomFriendDao roomFriendDao;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private ContactDao contactDao;
    @Autowired
    private HotRoomCache hotRoomCache;
    @Autowired
    private PushProducer pushProducer;

    @Override
    public void onMessage(MsgSendMessageDTO dto) {
        Message message = messageDao.getById(dto.getMsgId());
        Room room = roomCache.get(message.getRoomId());
        ChatMessageResp msgResp = chatService.getMsgResp(message, null);
        //所有房间更新房间最新消息
        roomDao.refreshActiveTime(room.getId(), message.getId(), message.getCreateTime());
        roomCache.delete(room.getId());
        if (room.isHotRoom()) {//热门群聊推送所有在线的人
            //更新热门群聊时间-redis
            hotRoomCache.refreshActiveTime(room.getId(), message.getCreateTime());
            //推送所有人
            pushProducer.sendPushMsg(WSAdapter.buildMsgSend(msgResp));
        } else {
            List<Long> memberUidList = new ArrayList<>();
            if (Objects.equals(room.getType(), RoomTypeEnum.GROUP.getType())) {//普通群聊推送所有群成员
                memberUidList = groupMemberCache.getMemberUidList(room.getId());
            } else if (Objects.equals(room.getType(), RoomTypeEnum.FRIEND.getType())) {//单聊对象
                //对单人推送
                RoomFriend roomFriend = roomFriendDao.getByRoomId(room.getId());
                memberUidList = Arrays.asList(roomFriend.getUid1(), roomFriend.getUid2());
            }
            //更新所有群成员的会话时间
            contactDao.refreshOrCreateActiveTime(room.getId(), memberUidList, message.getId(), message.getCreateTime());
            //推送房间成员
            pushProducer.sendPushMsg(WSAdapter.buildMsgSend(msgResp), memberUidList);
        }
    }


}
