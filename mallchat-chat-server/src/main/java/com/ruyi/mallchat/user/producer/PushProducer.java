package com.ruyi.mallchat.user.producer;

import com.ruyi.mallchat.common.constant.MQConstant;
import com.ruyi.mallchat.common.domain.dto.PushMessageDTO;
import com.ruyi.mallchat.transaction.service.MQProducer;
import com.ruyi.mallchat.websocket.domain.vo.response.WSBaseResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:  mq push用户消息 生产
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-08-12
 */
@Service
public class PushProducer {
    @Autowired
    private MQProducer mqProducer;

    /**
     * 推送给多个用户
     *
     * @param msg     ws响应消息
     * @param uidList 多个用户id
     */
    public void sendPushMsg(WSBaseResp<?> msg, List<Long> uidList) {
        uidList.parallelStream().forEach(uid -> {
            mqProducer.sendMsg(MQConstant.PUSH_TOPIC, new PushMessageDTO(uid, msg));
        });
    }

    /**
     * 推送给单个用户
     *
     * @param msg ws响应消息
     * @param uid 用户id
     */
    public void sendPushMsg(WSBaseResp<?> msg, Long uid) {
        mqProducer.sendMsg(MQConstant.PUSH_TOPIC, new PushMessageDTO(uid, msg));
    }

    /**
     * 推送给全部用户
     *
     * @param msg
     */
    public void sendPushMsg(WSBaseResp<?> msg) {
        mqProducer.sendMsg(MQConstant.PUSH_TOPIC, new PushMessageDTO(msg));
    }
}
