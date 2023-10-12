package com.ruyi.mallchat.common.common.event.listener;

import com.ruyi.mallchat.common.common.domain.enums.IdempotentEnum;
import com.ruyi.mallchat.common.common.event.UserRegisterEvent;
import com.ruyi.mallchat.common.user.dao.UserDao;
import com.ruyi.mallchat.common.user.domain.entity.User;
import com.ruyi.mallchat.common.user.domain.enums.ItemEnum;
import com.ruyi.mallchat.common.user.service.UserBackpackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 用户注册监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class UserRegisterListener {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserBackpackService userBackpackService;

    @Async
    @EventListener(classes = UserRegisterEvent.class)
    public void sendCard(UserRegisterEvent event) {
        User user = event.getUser();
        //送一张改名卡
        userBackpackService.acquireItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getId(), IdempotentEnum.UID, user.getId().toString());
    }

    @Async
    @EventListener(classes = UserRegisterEvent.class)
//    @TransactionalEventListener(classes = UserRegisterEvent.class,phase= TransactionPhase.AFTER_COMMIT)
    public void sendBadge(UserRegisterEvent event) {
        User user = event.getUser();
        int count = userDao.count();//todo 性能瓶颈，等注册用户多了直接删掉
        if (count <= 13) {//送一个前十名徽章(有3个系统预设用户)
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP10_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        } else if (count <= 103) {//送一个前百名徽章(有3个系统预设用户)
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP100_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        }
    }

}
