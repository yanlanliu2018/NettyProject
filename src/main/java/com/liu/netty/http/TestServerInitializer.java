package com.liu.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class TestServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //向管道中加入处理器

        //得到管道
        ChannelPipeline pipeline = ch.pipeline();
        //加入一个 netty 提供的 httpServerCodec codec =》[codec - decoder]
        //httpServerCodec 说明
        //1。 httpServerCodec 是netty 提供的处理http的编-解码器
        pipeline.addLast("MyHttpServerCodec",new HttpServerCodec());
        //2. 增加一个自定义的 handler
        pipeline.addLast("MyTestHttpServerHandler",new TestHttpServerHandler());

        System.out.println("ok~~~~~");
    }
}
