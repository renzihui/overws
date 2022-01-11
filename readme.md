## Move a Netty TCP application onto WebSocket

I need to support a new client running in browser. Thanks for the flexibility of Netty,  it can be simply achieved by just\
adding a few WebSocket handlers at the bottom of pipeline, without change old net code and business logic. 

```
    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        //a typical websocket protocol stack
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler("/", null, true));


        //wrap/unwrap ByteBuf to/from WebSocketFrame
        pipeline.addLast(new WebSocketReader());
        pipeline.addLast(new WebSocketWriter());


        // Then old pipeline
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        // the encoder and decoder are static as these are sharable
        pipeline.addLast(DECODER);
        pipeline.addLast(ENCODER);
        pipeline.addLast(SERVER_HANDLER);

```

### Attribution 
PlainText tcp sample and WebSocket code are copied from Github open source projects. Copyright belongs to the original authors.
