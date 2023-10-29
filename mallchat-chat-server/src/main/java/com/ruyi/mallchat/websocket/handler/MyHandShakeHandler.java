//package com.ruyi.mallchat.common.websocket.handler;
//
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelFutureListener;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//import io.netty.handler.codec.http.HttpObject;
//import io.netty.handler.codec.http.HttpRequest;
//import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
//import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
//import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
//import io.netty.util.Attribute;
//import io.netty.util.AttributeKey;
//
///**
// * Description: 自定义握手处理器,用协议来传token，不建议用
// * Author: <a href="https://github.com/zongzibinbin">abin</a>
// * Date: 2023-09-03
// */
//@Deprecated
//public class MyHandShakeHandler extends ChannelInboundHandlerAdapter {
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        final HttpObject httpObject = (HttpObject) msg;
//
//        if (httpObject instanceof HttpRequest) {
//            final HttpRequest req = (HttpRequest) httpObject;
//            String token = req.headers().get("Sec-WebSocket-Protocol");
//            Attribute<Object> token1 = ctx.channel().attr(AttributeKey.valueOf("token"));
//            token1.set(token);
//            final WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
//                    req.getUri(),
//                    token, false);
//            final WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
//            if (handshaker == null) {
//                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
//            } else {
//                // Ensure we set the handshaker and replace this handler before we
//                // trigger the actual handshake. Otherwise we may receive websocket bytes in this handler
//                // before we had a chance to replace it.
//                //
//                // See https://github.com/netty/netty/issues/9471.
//                ctx.pipeline().remove(this);
//
//                final ChannelFuture handshakeFuture = handshaker.handshake(ctx.channel(), req);
//                handshakeFuture.addListener(new ChannelFutureListener() {
//                    @Override
//                    public void operationComplete(ChannelFuture future) {
//                        if (!future.isSuccess()) {
//                            ctx.fireExceptionCaught(future.cause());
//                        } else {
//                            // Kept for compatibility
//                            ctx.fireUserEventTriggered(
//                                    WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);
//                        }
//                    }
//                });
//            }
//        } else
//            ctx.fireChannelRead(msg);
//    }
//}
