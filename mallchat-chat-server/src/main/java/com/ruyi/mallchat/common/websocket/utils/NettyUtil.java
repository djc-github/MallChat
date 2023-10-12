package com.ruyi.mallchat.common.websocket.utils;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * Description: netty工具类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-04-18
 */

public class NettyUtil {
    /**
     * websocket channel属性 token key
     */
    public static AttributeKey<String> TOKEN = AttributeKey.valueOf("token");
    /**
     * websocket channel属性 ip key
     */
    public static AttributeKey<String> IP = AttributeKey.valueOf("ip");
    /**
     * websocket channel属性 用户id key
     */
    public static AttributeKey<Long> UID = AttributeKey.valueOf("uid");
    public static AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY = AttributeKey.valueOf(WebSocketServerHandshaker.class, "HANDSHAKER");

    /**
     * 设置websocket channel属性
     *
     * @param channel      channel通道
     * @param attributeKey channel属性 键
     * @param data         值
     */
    public static <T> void setAttr(Channel channel, AttributeKey<T> attributeKey, T data) {
        Attribute<T> attr = channel.attr(attributeKey);
        attr.set(data);
    }

    /**
     * 获取websocket channel属性值
     *
     * @param channel      channel通道
     * @param attributeKey channel属性 键
     * @return 键对应的值
     */
    public static <T> T getAttr(Channel channel, AttributeKey<T> attributeKey) {
        return channel.attr(attributeKey).get();
    }

//    /**
//     * 设置websocket channel属性
//     * @param key 键
//     * @param data 值
//     */
//    public static <T> void setCustomAttr(Channel channel, String key, T data) {
//        AttributeKey<T> attributeKey = AttributeKey.valueOf(key);
//        Attribute<T> attr = channel.attr(attributeKey);
//        attr.set(data);
//    }
}
