package com.ruyi.mallchat.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 消息撤回的推送类
 *
 * @author <a href="https://github.com/djc-github">如意</a>
 * Date: 2023-10-02
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMsgRecallDTO {
    /**
     * 消息id
     */
    private Long msgId;
    /**
     * 房间id
     */
    private Long roomId;
    /**
     * 撤回的用户
     */
    private Long recallUid;
}
