package com.ruyi.mallchat.common.websocket;

import cn.hutool.json.JSONUtil;
import com.ruyi.mallchat.common.websocket.domain.enums.WSReqTypeEnum;
import com.ruyi.mallchat.common.websocket.domain.vo.request.WSBaseReq;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author djc
 * @version 1.0 2023/10/2 15:04
 * Description: TODO
 */
@Slf4j
@Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            // 读空闲
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                // 关闭用户的连接
                userOffLine(ctx);
            }
        }
        //如果握手完成(HandshakeComplete)
        else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            System.out.println("握手完成");
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 用户下线
     *
     * @param ctx
     */
    private void userOffLine(ChannelHandlerContext ctx) {
        //TODO 用户下线业务
        //断开websocket连接
        ctx.channel().close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame msg) throws Exception {
        WSBaseReq wsBaseReq = JSONUtil.toBean(msg.text(), WSBaseReq.class);
        WSReqTypeEnum wsReqTypeEnum = WSReqTypeEnum.of(wsBaseReq.getType());
        switch (wsReqTypeEnum) {
            case LOGIN:
                log.info("请求登录二维码 = " + msg.text());
                channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame("二维码"));
                break;
            case HEARTBEAT:
                break;
            case AUTHORIZE:
                break;
            default:
                log.info("未知类型");
        }
    }
}
