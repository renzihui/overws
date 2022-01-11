package org.example.ws.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.websocketx.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

public class WsClient {
    public static void main(String[] args) throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        URI uri = new URI("ws://localhost:8888/");
        try {

            final WebSocketClientHandshaker handshaker =
                    WebSocketClientHandshakerFactory.newHandshaker(
                            uri, WebSocketVersion.V13, null, false, EmptyHttpHeaders.INSTANCE);

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(uri.getHost(), uri.getPort())
                    .handler(new ClientInitializer(handshaker));

            System.out.println("WebSocket Client connecting");
            Channel ch = b.connect().sync().channel();
            handshaker.handshake(ch).sync();

            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (; ; ) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                ch.writeAndFlush(new TextWebSocketFrame(line + "\r\n"));
                // If user typed the 'bye' command, wait until the server closes
                // the connection.
                if ("bye".equals(line.toLowerCase())) {
                    ch.closeFuture().sync();
                    break;
                }
            }

            // Wait until all messages are flushed before closing the channel.
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }

//            // Send 10 messages and wait for responses
//            System.out.println("WebSocket Client sending message");
//            for (int i = 0; i < 1000; i++) {
//                ch.write(new TextWebSocketFrame("Message #" + i));
//            }
//
//            // Ping
//            System.out.println("WebSocket Client sending ping");
//            ch.write(new PingWebSocketFrame(Unpooled.copiedBuffer(new byte[]{1, 2, 3, 4, 5, 6})));
//
//            // Close
//            System.out.println("WebSocket Client sending close");
//            ch.write(new CloseWebSocketFrame());

            // WebSocketClientHandler will close the connection when the server
            // responds to the CloseWebSocketFrame.
            ch.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

}
