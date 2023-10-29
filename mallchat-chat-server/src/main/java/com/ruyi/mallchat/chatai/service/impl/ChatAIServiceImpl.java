package com.ruyi.mallchat.chatai.service.impl;

import com.ruyi.mallchat.chat.domain.entity.Message;
import com.ruyi.mallchat.chat.domain.entity.msg.MessageExtra;
import com.ruyi.mallchat.chatai.handler.AbstractChatAIHandler;
import com.ruyi.mallchat.chatai.handler.ChatAIHandlerFactory;
import com.ruyi.mallchat.chatai.service.ChatAIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatAIServiceImpl implements ChatAIService {
    @Override
    public void chat(Message message) {
        MessageExtra extra = message.getExtra();
        if (extra == null) {
            return;
        }
//        AbstractChatAIHandler chatAI = ChatAIHandlerFactory.getChatAIHandlerByName(message.getContent());
        AbstractChatAIHandler chatAI = ChatAIHandlerFactory.getChatAIHandlerById(extra.getAtUidList());
        if (chatAI != null) {
            chatAI.chat(message);
        }
    }
}