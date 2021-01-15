package com.liu.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class MyServer {

    public static void main(String[] args) throws Exception{

        //创建两个线程组
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(); // 默认是 8 个NioEventLoop
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO)) // 在bossGroup 增加一个日志处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //加入一个 netty 提供的 IdleStateHandler
                            /*
                            说明：
                            1. IdleStateHandler 是 netty 提供的处理空闲状态的处理器
                            2. long readerIdleTime：表示多长时间没有读，就会发送一个心态检测包检测是否连接；
                            3. long writerIdleTime：表示多长时间灭有写，就会发送一个心态检测包检测是否连接；
                            4. long allIdleTime：表示多长时间没有读写，就会发送一个心态检测包检测是否连接；
                            5. 文档说明：
                            Triggers an {@link IdleStateEvent} when a {@link Channel} has not performed read, write, or both operation for a while.
                            6. 当 IdleStateEvent 触发后，就会传递给管道的下一个 handler 处理通过调用（触发）下一个 handler 的userEventTriggered ，
                             在该方法中处理 IdleStateEvent（读空闲、写空闲、读写空闲）
                             */

                            pipeline.addLast(new IdleStateHandler(3,5,7, TimeUnit.SECONDS));
                            // 加入一个对空闲检测进一步处理的handler（自定义）
                            pipeline.addLast(null);
                        }
                    });

            // 启动服务器
            ChannelFuture channelFuture = serverBootstrap.bind(7000).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
