package com.ruyi.mallchat.websocket.handler;

import cn.hutool.core.net.url.UrlBuilder;
import com.ruyi.mallchat.websocket.utils.NettyUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.Optional;

public class HeadersCollectHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            //获取url
            FullHttpRequest request = (FullHttpRequest) msg;
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());

            // 从url查询参数中获取token
            String token = Optional.ofNullable(urlBuilder.getQuery())
                    .map(urlQuery -> urlQuery.get("token"))
                    .map(CharSequence::toString)
                    .orElse("");
            NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, token);

            // 重置请求路径，去掉查询参数
            request.setUri(urlBuilder.getPath().toString());
            //获取nginx记录的ip
            HttpHeaders headers = request.headers();
            String ip = headers.get("X-Real-IP");
            //如果没经过nginx，就直接获取远端地址
            if (StringUtils.isEmpty(ip)) {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                ip = address.getAddress().getHostAddress();
            }
            NettyUtil.setAttr(ctx.channel(), NettyUtil.IP, ip);
            //把本handler从这个channel移除（每个channel只处理一次）
            ctx.pipeline().remove(this);
            ctx.fireChannelRead(request);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private String getQueryValue(ChannelHandlerContext ctx, UrlBuilder urlBuilder, String queryKey) {
        String queryValue = Optional.ofNullable(urlBuilder.getQuery())
                .map(urlQuery -> urlQuery.get(queryKey))
                .map(CharSequence::toString)
                .orElse("");
        NettyUtil.setAttr(ctx.channel(), AttributeKey.valueOf(queryKey), queryValue);
        return queryValue;
    }
}