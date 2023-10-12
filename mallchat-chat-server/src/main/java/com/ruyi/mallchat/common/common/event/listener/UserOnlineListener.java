package com.ruyi.mallchat.common.common.event.listener;

import com.ruyi.mallchat.common.common.event.UserOnlineEvent;
import com.ruyi.mallchat.common.user.dao.UserDao;
import com.ruyi.mallchat.common.user.domain.entity.User;
import com.ruyi.mallchat.common.user.domain.enums.ChatActiveStatusEnum;
import com.ruyi.mallchat.common.user.producer.PushProducer;
import com.ruyi.mallchat.common.user.service.IpService;
import com.ruyi.mallchat.common.user.service.cache.UserCache;
import com.ruyi.mallchat.common.websocket.service.WebSocketService;
import com.ruyi.mallchat.common.websocket.service.adapter.WSAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 用户上线监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class UserOnlineListener {
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserCache userCache;
    @Autowired
    private WSAdapter wsAdapter;
    @Autowired
    private IpService ipService;
    @Autowired
    private PushProducer producer;

    /**
     * 更新redis在线列表并推送前端
     */
    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void saveRedisAndPush(UserOnlineEvent event) {
        User user = event.getUser();
        //更新 在线/离线用户列表缓存
        userCache.online(user.getId(), user.getLastOptTime());
        //推送给所有在线用户，该用户登录成功
        producer.sendPushMsg(wsAdapter.buildOnlineNotifyResp(event.getUser()));
    }

    /**
     * 更新数据库
     */
    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void saveDB(UserOnlineEvent event) {
        //更新用户基本信息（最后上下线时间、ip信息和在线状态）
        User user = event.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setIpInfo(user.getIpInfo());
        update.setActiveStatus(ChatActiveStatusEnum.ONLINE.getStatus());
        userDao.updateById(update);
        //更新用户ip详情
        ipService.refreshIpDetailAsync(user.getId());
    }

}
