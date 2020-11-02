package com.liu.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

/*
说明：
1. 我们自定义一个 Handler 需要继承 netty 规定好的某个 HandlerAdapter （规范）
2. 此时，我们自定义的 Handler 才可以成为一个 handler
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    //读取数据事件（在这里可以读取客户端发送的消息）
    /*
    1. ChannelHanlerContext ctx：上下文对象，含有管道 pipeline ， 通道 channel ， 地址
    ps：通道与管道的区别：
            通道：主要用于数据的传输
            管道：主要用于数据的处理，连接多个处理数据的 handler

    2. Object msg ：就是客户端发送的数据  默认Object
     */

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("服务器读取线程："+Thread.currentThread().getName());
        System.out.println("server ctx ="+ctx);
        System.out.println("看看channel 和 pipeline 的关系");
        Channel channel = ctx.channel();
        ChannelPipeline pipeline = ctx.pipeline();  //本质是一个双向链表，出栈入栈
        //将 msg 转成一个 ByteBuf
        //ByteBuf 是Netty 提供的，不是 NIO 的ByteBuffer。
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("客户端发送的消息是："+buf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址："+ctx.channel().remoteAddress());
    }

    //数据读取完毕
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //writeAndFlush 是 write + flush
        //将数据写入到缓存，并刷新
        //一般来讲，我们对这个发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端~",CharsetUtil.UTF_8));
    }

    //处理异常，一般是需要关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
