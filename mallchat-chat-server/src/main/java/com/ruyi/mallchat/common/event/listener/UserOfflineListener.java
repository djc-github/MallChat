package com.ruyi.mallchat.common.event.listener;

import com.ruyi.mallchat.common.event.UserOfflineEvent;
import com.ruyi.mallchat.user.dao.UserDao;
import com.ruyi.mallchat.user.domain.entity.User;
import com.ruyi.mallchat.user.domain.enums.ChatActiveStatusEnum;
import com.ruyi.mallchat.user.service.cache.UserCache;
import com.ruyi.mallchat.websocket.service.WebSocketService;
import com.ruyi.mallchat.websocket.service.adapter.WSAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 用户下线监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class UserOfflineListener {
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserCache userCache;
    @Autowired
    private WSAdapter wsAdapter;

    /**
     * 更新redis在线用户列表并推送前端
     */
    @Async
    @EventListener(classes = UserOfflineEvent.class)
    public void saveRedisAndPush(UserOfflineEvent event) {
        User user = event.getUser();
        //更新 在线/离线用户列表缓存
        userCache.offline(user.getId(), user.getLastOptTime());
        //推送给所有在线用户，该用户下线
        webSocketService.sendToAllOnline(wsAdapter.buildOfflineNotifyResp(event.getUser()), event.getUser().getId());
    }

    /**
     * 更新数据库
     */
    @Async
    @EventListener(classes = UserOfflineEvent.class)
    public void saveDB(UserOfflineEvent event) {
        //更新用户基本信息（最后上下线时间和在线状态）
        User user = event.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setActiveStatus(ChatActiveStatusEnum.OFFLINE.getStatus());
        userDao.updateById(update);
    }

}
