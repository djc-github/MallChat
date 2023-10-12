//package com.ruyi.mallchat.common.common.event.listener;
//
//import com.ruyi.mallchat.common.common.event.UserBlackEvent;
//
//import com.ruyi.mallchat.common.user.domain.vo.response.ws.WSBlack;
//import com.ruyi.mallchat.common.user.service.cache.UserCache;
//import com.ruyi.mallchat.common.websocket.domain.enums.WSRespTypeEnum;
//import com.ruyi.mallchat.common.websocket.domain.vo.response.WSBaseResp;
//import com.ruyi.mallchat.common.websocket.service.WebSocketService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.event.EventListener;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
///**
// * 用户拉黑监听器
// *
// * @author zhongzb create on 2022/08/26
// */
//@Slf4j
//@Component
//public class UserBlackListener {
//    @Autowired
//    private MessageDao messageDao;
//    @Autowired
//    private WebSocketService webSocketService;
//    @Autowired
//    private UserCache userCache;
//
//    @Async
//    @EventListener(classes = UserBlackEvent.class)
//    public void refreshRedis(UserBlackEvent event) {
//        userCache.evictBlackMap();
//        userCache.remove(event.getUser().getId());
//    }
//
//    @Async
//    @EventListener(classes = UserBlackEvent.class)
//    public void deleteMsg(UserBlackEvent event) {
//        messageDao.invalidByUid(event.getUser().getId());
//    }
//
//    @Async
//    @EventListener(classes = UserBlackEvent.class)
//    public void sendPush(UserBlackEvent event) {
//        Long uid = event.getUser().getId();
//        WSBaseResp<WSBlack> resp = new WSBaseResp<>();
//        WSBlack black = new WSBlack(uid);
//        resp.setData(black);
//        resp.setType(WSRespTypeEnum.BLACK.getType());
//        webSocketService.sendToAllOnline(resp, uid);
//    }
//
//
//}
