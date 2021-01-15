package com.liu.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;

public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {


    //定义一个 channle 组，管理所有的 channle
    // GlobalEventExecutor.INSTANCE 是全局的事件执行器，是一个单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // handlerAdded 表示连接建立，一旦连接，第一个别执行
    // 将当前的 channel 加入到 channel Group 中
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //将该客户加入聊天的信息推送给其他在线的客户端
        /*
        该方法会将 channelGroup 中所有的channel 都便利一遍，并发送消息
        我们不需要自己遍历
         */
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + "加入聊天 " + simpleDateFormat.format(new java.util.Date()) + "\n");
        channelGroup.add(channel);
    }

    // 断开连接，将 xx 客户离开的消息推送给当前在线的客户
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + "离开了\n");
        System.out.println("channelGroup size " + channelGroup.size());
    }

    // 表示 channel 处于一个活动的状态，提示 xx 上线
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "上线了！");
    }

    // 表示 channel 处于不活动状态，提示xx离线了
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "离线了！");
    }

    // 读取数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        // 获取到当前 channel
        Channel channel = ctx.channel();
        // 遍历 channelGroup ，根据不同情况，回复不同消息
        channelGroup.forEach(ch->{
            if(channel != ch){// 不是当前的 channel ，转发消息
                ch.writeAndFlush("[客户]" + channel.remoteAddress() + "发送了消息" + msg + "\n");
            }else {
                ch.writeAndFlush("[自己]发送了消息" + msg + "\n");
            }
        });
    }

    // 异常处理

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 关闭通道
        ctx.close();
    }
}
