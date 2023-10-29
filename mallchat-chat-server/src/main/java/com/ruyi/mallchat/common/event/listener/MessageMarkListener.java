package com.ruyi.mallchat.common.event.listener;


import com.ruyi.mallchat.chat.dao.MessageDao;
import com.ruyi.mallchat.chat.dao.MessageMarkDao;
import com.ruyi.mallchat.chat.domain.dto.ChatMessageMarkDTO;
import com.ruyi.mallchat.chat.domain.entity.Message;
import com.ruyi.mallchat.chat.domain.enums.MessageMarkTypeEnum;
import com.ruyi.mallchat.chat.domain.enums.MessageTypeEnum;
import com.ruyi.mallchat.common.domain.enums.IdempotentEnum;
import com.ruyi.mallchat.common.event.MessageMarkEvent;
import com.ruyi.mallchat.user.domain.enums.ItemEnum;
import com.ruyi.mallchat.user.service.UserBackpackService;
import com.ruyi.mallchat.websocket.service.WebSocketService;
import com.ruyi.mallchat.websocket.service.adapter.WSAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

/**
 * 消息标记监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class MessageMarkListener {
    @Autowired
    private MessageMarkDao messageMarkDao;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private UserBackpackService iUserBackpackService;
    @Autowired
    private WebSocketService webSocketService;

    @Async
    @TransactionalEventListener(classes = MessageMarkEvent.class, fallbackExecution = true)
    public void changeMsgType(MessageMarkEvent event) {
        ChatMessageMarkDTO dto = event.getDto();
        Message msg = messageDao.getById(dto.getMsgId());
        if (!Objects.equals(msg.getType(), MessageTypeEnum.TEXT.getType())) {//普通消息才需要升级
            return;
        }
        //消息被标记次数
        Integer markCount = messageMarkDao.getMarkCount(dto.getMsgId(), dto.getMarkType());
        MessageMarkTypeEnum markTypeEnum = MessageMarkTypeEnum.of(dto.getMarkType());
        if (markCount < markTypeEnum.getRiseNum()) {
            return;
        }
        if (MessageMarkTypeEnum.LIKE.getType().equals(dto.getMarkType())) {//尝试给用户发送一张徽章
            iUserBackpackService.acquireItem(msg.getFromUid(), ItemEnum.LIKE_BADGE.getId(), IdempotentEnum.MSG_ID, msg.getId().toString());
        }
    }

    @Async
    @TransactionalEventListener(classes = MessageMarkEvent.class, fallbackExecution = true)
    public void notifyAll(MessageMarkEvent event) {//后续可做合并查询，目前异步影响不大
        ChatMessageMarkDTO dto = event.getDto();
        Integer markCount = messageMarkDao.getMarkCount(dto.getMsgId(), dto.getMarkType());
        webSocketService.sendToAllOnline(WSAdapter.buildMsgMarkSend(dto, markCount), dto.getUid());
    }

}
