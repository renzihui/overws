package org.example.ws.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketReader extends SimpleChannelInboundHandler<WebSocketFrame> {
    public WebSocketReader() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, WebSocketFrame webSocketFrame) throws Exception {
        channelHandlerContext.fireChannelRead(webSocketFrame.content().copy());
    }

}
