//package com.ruyi.mallchat.common.common.event.listener;
//
//import com.ruyi.mallchat.common.websocket.service.WebSocketService;
//import com.ruyi.mallchat.common.common.event.UserApplyEvent;
//import com.ruyi.mallchat.common.user.dao.UserApplyDao;
//import com.ruyi.mallchat.common.user.domain.entity.UserApply;
//import com.ruyi.mallchat.common.user.domain.vo.response.ws.WSFriendApply;
//import com.ruyi.mallchat.common.websocket.service.adapter.WSAdapter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.event.TransactionalEventListener;
//import com.ruyi.mallchat.common.user.producer.PushProducer;
///**
// * 好友申请监听器
// *
// * @author zhongzb create on 2022/08/26
// */
//@Slf4j
//@Component
//public class UserApplyListener {
//    @Autowired
//    private UserApplyDao userApplyDao;
//    @Autowired
//    private WebSocketService webSocketService;
//
//    @Autowired
//    private PushProducer producer;
//
//    @Async
//    @TransactionalEventListener(classes = UserApplyEvent.class, fallbackExecution = true)
//    public void notifyFriend(UserApplyEvent event) {
//        UserApply userApply = event.getUserApply();
//        Integer unReadCount = userApplyDao.getUnReadCount(userApply.getTargetId());
//        producer.sendPushMsg(WSAdapter.buildApplySend(new WSFriendApply(userApply.getUid(), unReadCount)), userApply.getTargetId());
//    }
//
//}
