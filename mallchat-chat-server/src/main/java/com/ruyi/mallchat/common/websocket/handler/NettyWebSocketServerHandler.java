package com.ruyi.mallchat.common.websocket.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.ruyi.mallchat.common.user.domain.vo.request.ws.WSAuthorize;
import com.ruyi.mallchat.common.websocket.domain.enums.WSReqTypeEnum;
import com.ruyi.mallchat.common.websocket.domain.vo.request.WSBaseReq;
import com.ruyi.mallchat.common.websocket.service.WebSocketService;
import com.ruyi.mallchat.common.websocket.utils.NettyUtil;
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
    private WebSocketService webSocketService;

    // 当web客户端连接后，触发该方法
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.webSocketService = getService();
    }

    // 客户端离线
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        userOffLine(ctx);
    }

    /**
     * 取消绑定
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 可能出现业务判断离线后再次触发 channelInactive
        log.warn("触发 channelInactive 掉线![{}]", ctx.channel().id());
        userOffLine(ctx);
    }

    /**
     * 用户下线并关闭channel
     *
     * @param ctx
     */
    private void userOffLine(ChannelHandlerContext ctx) {
        this.webSocketService.removed(ctx.channel());
        ctx.channel().close();
    }

    /**
     * 心跳检查
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            // 读空闲
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                // 关闭用户的连接
                userOffLine(ctx);
            }
        }//握手完成
        else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            //TODO:疑问：目前了解的作用是： 记录当前websocket的{channel映射信息}表，key为chanel映射信息暂无，具体目的？
            this.webSocketService.connect(ctx.channel());
            //获取传入的token
            String token = NettyUtil.getAttr(ctx.channel(), NettyUtil.TOKEN);
            if (StrUtil.isNotBlank(token)) {//如果有token则认证
                this.webSocketService.authorize(ctx.channel(), new WSAuthorize(token));
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    // 处理异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("异常发生，异常消息 ={}", cause);
        ctx.channel().close();
    }

    private WebSocketService getService() {
        return SpringUtil.getBean(WebSocketService.class);
    }

    // 读取客户端发送的请求报文
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame msg) throws Exception {
        String msgText = msg.text();
        WSBaseReq wsBaseReq = JSONUtil.toBean(msgText, WSBaseReq.class);
        WSReqTypeEnum wsReqTypeEnum = WSReqTypeEnum.of(wsBaseReq.getType());
        switch (wsReqTypeEnum) {
            case LOGIN:
                log.info("请求二维码 = " + msgText);
                this.webSocketService.handleLoginReq(channelHandlerContext.channel());
                break;
            case HEARTBEAT:
                break;
            case AUTHORIZE:
                log.info("登录认证 = " + msgText);
//                this.webSocketService.authorize(channelHandlerContext.channel(),WSAuthorize.builder().token(wsBaseReq.getData()).build());
                break;
            default:
                log.info("未知类型");
        }
    }
}
