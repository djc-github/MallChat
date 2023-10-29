package com.ruyi.mallchat.websocket.domain.vo.response;

import com.ruyi.mallchat.websocket.domain.enums.WSRespTypeEnum;
import lombok.Data;

/**
 * Description: ws的基本返回信息体
 *
 * @author <a href="https://github.com/djc-github">ruyi</a>
 * Date: 2023-10-02
 */
@Data
public class WSBaseResp<T> {
    /**
     * ws推送给前端的消息类型
     *
     * @see WSRespTypeEnum
     */
    private Integer type;
    private T data;
}
