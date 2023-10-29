package com.ruyi.mallchat.common.event.listener;

import com.ruyi.mallchat.chat.domain.dto.ChatMsgRecallDTO;
import com.ruyi.mallchat.chat.service.ChatService;
import com.ruyi.mallchat.chat.service.cache.MsgCache;
import com.ruyi.mallchat.common.event.MessageRecallEvent;
import com.ruyi.mallchat.user.producer.PushProducer;
import com.ruyi.mallchat.websocket.service.WebSocketService;
import com.ruyi.mallchat.websocket.service.adapter.WSAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 消息撤回监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class MessageRecallListener {
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private MsgCache msgCache;
    @Autowired
    private PushProducer pushProducer;

    @Async
    @TransactionalEventListener(classes = MessageRecallEvent.class, fallbackExecution = true)
    public void evictMsg(MessageRecallEvent event) {
        ChatMsgRecallDTO recallDTO = event.getRecallDTO();
        msgCache.evictMsg(recallDTO.getMsgId());
    }

    @Async
    @TransactionalEventListener(classes = MessageRecallEvent.class, fallbackExecution = true)
    public void sendToAll(MessageRecallEvent event) {
        pushProducer.sendPushMsg(WSAdapter.buildMsgRecall(event.getRecallDTO()));
    }

}
