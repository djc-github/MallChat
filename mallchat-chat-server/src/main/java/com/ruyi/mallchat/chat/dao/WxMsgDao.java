package com.ruyi.mallchat.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruyi.mallchat.chat.domain.entity.msg.WxMsg;
import com.ruyi.mallchat.chat.mapper.WxMsgMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 微信消息表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-05-16
 */
@Service
public class WxMsgDao extends ServiceImpl<WxMsgMapper, WxMsg> {

}
