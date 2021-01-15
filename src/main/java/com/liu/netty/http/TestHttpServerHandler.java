package com.liu.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/*
说明：
1.SimpleChannelInboundHandler 是 ChannelInboundHandlerAdapter 子类
2. HttpObject 客户端和服务端户型啊通讯的数据被封装成 HttpObject
 */

public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    //channelRead0 读取客户端数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        System.out.println("对应的channel="+ ctx.channel() + "pipeline=" + ctx.pipeline() +
                "通过pipeline 获取 channel " + ctx.pipeline().channel());

        System.out.println("当前ctx的handler = " + ctx.handler());

        //判断msg是不是 httpRequest 请求
        if(msg instanceof HttpRequest){

            System.out.println("ctx 的类型："+ctx.getClass());

            System.out.println("pipeline hashcode "+ ctx.pipeline().hashCode() +
                    " TestHttpServerHandler hash=" + this.hashCode());

            System.out.println("msg 类型=" + msg.getClass());
            System.out.println("客户端地址："+ ctx.channel().remoteAddress());

            //获取uri，并对部分请求进行过滤
            HttpRequest httpRequest = (HttpRequest)msg;
            URI uri = new URI(httpRequest.uri());
            if("/favicon.ico".equals(uri.getPath())){
                System.out.println("请求了 /favicon.ico， 不做响应");
                return;
            }

            //回复信息给浏览器[http协议]
            ByteBuf content = Unpooled.copiedBuffer("hello,我是服务器", CharsetUtil.UTF_8);
            //构造一个http的响应，即 httpResponse
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain;charset=UTF-8");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());
            //将构建好的 response 返回
            ctx.writeAndFlush(response);
        }
    }
}
