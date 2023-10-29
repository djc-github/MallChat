package com.ruyi.mallchat.chat.service.cache;

import com.ruyi.mallchat.chat.dao.RoomFriendDao;
import com.ruyi.mallchat.chat.domain.entity.RoomFriend;
import com.ruyi.mallchat.common.constant.RedisKey;
import com.ruyi.mallchat.common.service.cache.AbstractRedisStringCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 群组基本信息的缓存
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-10
 */
@Component
public class RoomFriendCache extends AbstractRedisStringCache<Long, RoomFriend> {
    @Autowired
    private RoomFriendDao roomFriendDao;

    @Override
    protected String getKey(Long groupId) {
        return RedisKey.getKey(RedisKey.GROUP_INFO_STRING, groupId);
    }

    @Override
    protected Long getExpireSeconds() {
        return 5 * 60L;
    }

    @Override
    protected Map<Long, RoomFriend> load(List<Long> roomIds) {
        List<RoomFriend> roomGroups = roomFriendDao.listByRoomIds(roomIds);
        return roomGroups.stream().collect(Collectors.toMap(RoomFriend::getRoomId, Function.identity()));
    }
}
