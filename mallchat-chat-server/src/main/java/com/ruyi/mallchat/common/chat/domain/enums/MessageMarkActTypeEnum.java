package com.ruyi.mallchat.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 消息标记动作类型
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@AllArgsConstructor
@Getter
public enum MessageMarkActTypeEnum {
    MARK(1, "确认标记"),
    UN_MARK(2, "取消标记"),
    ;

    private static Map<Integer, MessageMarkActTypeEnum> cache;

    static {
        cache = Arrays.stream(MessageMarkActTypeEnum.values()).collect(Collectors.toMap(MessageMarkActTypeEnum::getType, Function.identity()));
    }

    private final Integer type;
    private final String desc;

    public static MessageMarkActTypeEnum of(Integer type) {
        return cache.get(type);
    }
}
