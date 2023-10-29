package com.ruyi.mallchat.chat.service;

import com.ruyi.mallchat.chat.domain.vo.request.*;
import com.ruyi.mallchat.chat.domain.vo.response.ChatMemberListResp;
import com.ruyi.mallchat.chat.domain.vo.response.ChatRoomResp;
import com.ruyi.mallchat.chat.domain.vo.response.MemberResp;
import com.ruyi.mallchat.common.domain.vo.request.CursorPageBaseReq;
import com.ruyi.mallchat.common.domain.vo.response.CursorPageBaseResp;
import com.ruyi.mallchat.websocket.domain.vo.response.ChatMemberResp;

import java.util.List;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-07-22
 */
public interface RoomAppService {
    /**
     * 获取会话列表--支持未登录态
     */
    CursorPageBaseResp<ChatRoomResp> getContactPage(CursorPageBaseReq request, Long uid);

    /**
     * 获取群组信息
     */
    MemberResp getGroupDetail(Long uid, long roomId);

    CursorPageBaseResp<ChatMemberResp> getMemberPage(MemberReq request);

    List<ChatMemberListResp> getMemberList(ChatMessageMemberReq request);

    void delMember(Long uid, MemberDelReq request);

    void addMember(Long uid, MemberAddReq request);

    Long addGroup(Long uid, GroupAddReq request);

    ChatRoomResp getContactDetail(Long uid, Long roomId);

    ChatRoomResp getContactDetailByFriend(Long uid, Long friendUid);
}
