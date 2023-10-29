package com.ruyi.mallchat.user.service;

import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Service;

/**
 * Description: 处理与微信api的交互逻辑
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Service
public interface WxMsgService {
    /**
     * 用户扫码
     *
     * @return 用户授权链接消息
     */
    WxMpXmlOutMessage scan(WxMpService wxMpService, WxMpXmlMessage wxMpXmlMessage);

    /**
     * 用户授权
     *
     * @param userInfo 微信用户信息
     */
    public void authorize(WxOAuth2UserInfo userInfo);

}
