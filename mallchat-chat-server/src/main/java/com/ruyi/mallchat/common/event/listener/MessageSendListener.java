package com.ruyi.mallchat.common.event.listener;

import com.ruyi.mallchat.chat.dao.ContactDao;
import com.ruyi.mallchat.chat.dao.MessageDao;
import com.ruyi.mallchat.chat.dao.RoomDao;
import com.ruyi.mallchat.chat.dao.RoomFriendDao;
import com.ruyi.mallchat.chat.domain.entity.Message;
import com.ruyi.mallchat.chat.domain.entity.Room;
import com.ruyi.mallchat.chat.domain.enums.HotFlagEnum;
import com.ruyi.mallchat.chat.service.ChatService;
import com.ruyi.mallchat.chat.service.WeChatMsgOperationService;
import com.ruyi.mallchat.chat.service.cache.GroupMemberCache;
import com.ruyi.mallchat.chat.service.cache.HotRoomCache;
import com.ruyi.mallchat.chat.service.cache.RoomCache;
import com.ruyi.mallchat.chatai.service.ChatAIService;
import com.ruyi.mallchat.common.constant.MQConstant;
import com.ruyi.mallchat.common.domain.dto.MsgSendMessageDTO;
import com.ruyi.mallchat.common.event.MessageSendEvent;
import com.ruyi.mallchat.transaction.service.MQProducer;
import com.ruyi.mallchat.user.service.cache.UserCache;
import com.ruyi.mallchat.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

/**
 * 消息发送监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class MessageSendListener {
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
    private MQProducer mqProducer;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, classes = MessageSendEvent.class, fallbackExecution = true)
    public void messageRoute(MessageSendEvent event) {
        Long msgId = event.getMsgId();
        mqProducer.sendSecureMsg(MQConstant.SEND_MSG_TOPIC, new MsgSendMessageDTO(msgId), msgId);
    }

    @TransactionalEventListener(classes = MessageSendEvent.class, fallbackExecution = true)
    public void handlerMsg(@NotNull MessageSendEvent event) {
        Message message = messageDao.getById(event.getMsgId());
        Room room = roomCache.get(message.getRoomId());
        if (isHotRoom(room)) {
            openAIService.chat(message);
        }
    }

    public boolean isHotRoom(Room room) {
        return Objects.equals(HotFlagEnum.YES.getType(), room.getHotFlag());
    }

    /**
     * 给用户微信推送艾特好友的消息通知
     * （这个没开启，微信不让推）
     */
    @TransactionalEventListener(classes = MessageSendEvent.class, fallbackExecution = true)
    public void publishChatToWechat(@NotNull MessageSendEvent event) {
        Message message = messageDao.getById(event.getMsgId());
        if (Objects.nonNull(message.getExtra().getAtUidList())) {
            weChatMsgOperationService.publishChatMsgToWeChatUser(message.getFromUid(), message.getExtra().getAtUidList(),
                    message.getContent());
        }
    }
}
