package com.ruyi.mallchat.common.constant;

/**
 * @author zhongzb create on 2021/06/10
 */
public interface MQConstant {

    /**
     * mq消息发送 topic
     */
    String SEND_MSG_TOPIC = "chat_send_msg";
    /**
     * mq消息发送 group
     */
    String SEND_MSG_GROUP = "chat_send_msg_group";

////////////////////////////////////////////////////////////////////////
    /**
     * push用户  topic
     */
    String PUSH_TOPIC = "websocket_push";
    /**
     * push用户  group
     */
    String PUSH_GROUP = "websocket_push_group";

////////////////////////////////////////////////////////////////////////
    /**
     * mq(授权完成后)登录信息  topic
     */
    String LOGIN_MSG_TOPIC = "user_login_send_msg";
    /**
     * mq(授权完成后)登录信息  group
     */
    String LOGIN_MSG_GROUP = "user_login_send_msg_group";

////////////////////////////////////////////////////////////////////////
    /**
     * mq扫码成功信息 topic
     */
    String SCAN_MSG_TOPIC = "user_scan_send_msg";
    /**
     * mq扫码成功信息 group
     */
    String SCAN_MSG_GROUP = "user_scan_send_msg_group";
}
