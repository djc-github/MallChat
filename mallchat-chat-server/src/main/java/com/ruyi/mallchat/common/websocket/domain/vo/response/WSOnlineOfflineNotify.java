package com.ruyi.mallchat.common.websocket.domain.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:用户上下线变动的推送类
 *
 * @author <a href="https://github.com/djc-github">ruyi</a>
 * Date: 2023-10-02
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSOnlineOfflineNotify {
    /**
     * 新的上下线用户
     */
    private List<ChatMemberResp> changeList = new ArrayList<>();
    /**
     * 在线人数
     */
    private Long onlineNum;
}
