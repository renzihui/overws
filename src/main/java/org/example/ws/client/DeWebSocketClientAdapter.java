package org.example.ws.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class DeWebSocketClientAdapter extends ChannelInboundHandlerAdapter {

    private final WebSocketClientHandshaker handshaker;

    public DeWebSocketClientAdapter(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(ch, (FullHttpResponse) message);
            System.out.println("WebSocket Client connected!");
            return;
        }

        if (message instanceof WebSocketFrame) {
            super.channelRead(ctx, ((WebSocketFrame) message).content());
        } else if (message instanceof PongWebSocketFrame) {
            System.out.println("WebSocket Client received pong");
        } else if (message instanceof CloseWebSocketFrame) {
            System.out.println("WebSocket Client received closing");
            ctx.close();
        } else {
            System.out.println("Invalid message format");
        }
    }
}
