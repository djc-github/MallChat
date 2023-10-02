package com.ruyi.mallchat.common.websocket.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: ws前端请求类型枚举
 *
 * @author <a href="https://github.com/djc-github">ruyi</a>
 * Date: 2023-10-02
 */
@AllArgsConstructor
@Getter
public enum WSReqTypeEnum {
    LOGIN(1, "请求登录二维码"),
    HEARTBEAT(2, "心跳包"),
    AUTHORIZE(3, "登录认证"),
    ;
    private static Map<Integer, WSReqTypeEnum> cache;

    static {
        cache = Arrays.stream(WSReqTypeEnum.values()).collect(Collectors.toMap(WSReqTypeEnum::getType, Function.identity()));
    }

    /**
     * 请求类型
     */
    private final Integer type;
    /**
     * 请求类型描述
     */
    private final String desc;

    public static WSReqTypeEnum of(Integer type) {
        return cache.get(type);
    }
}