package com.ruyi.mallchat.common.websocket.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 聊天状态类型枚举
 *
 * @author <a href="https://github.com/djc-github">ruyi</a>
 * Date: 2023-10-02
 */
@AllArgsConstructor
@Getter
public enum ChatActiveStatusEnum {
    ONLINE(1, "在线"),
    OFFLINE(2, "离线"),
    ;

    private static Map<Integer, ChatActiveStatusEnum> cache;

    static {
        cache = Arrays.stream(ChatActiveStatusEnum.values()).collect(Collectors.toMap(ChatActiveStatusEnum::getStatus, Function.identity()));
    }

    private final Integer status;
    private final String desc;

    public static ChatActiveStatusEnum of(Integer type) {
        return cache.get(type);
    }
}
