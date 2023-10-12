package com.ruyi.mallchat.common.common.constant;

/**
 * @author zhongzb create on 2021/06/10
 */
public interface MQConstant {

    /**
     * 消息发送mq topic
     */
    String SEND_MSG_TOPIC = "chat_send_msg";
    /**
     * 消息发送mq group
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
     * (授权完成后)登录信息mq  topic
     */
    String LOGIN_MSG_TOPIC = "user_login_send_msg";
    /**
     * (授权完成后)登录信息mq  group
     */
    String LOGIN_MSG_GROUP = "user_login_send_msg_group";

////////////////////////////////////////////////////////////////////////
    /**
     * 扫码成功信息mq topic
     */
    String SCAN_MSG_TOPIC = "user_scan_send_msg";
    /**
     * 扫码成功信息mq group
     */
    String SCAN_MSG_GROUP = "user_scan_send_msg_group";
}
