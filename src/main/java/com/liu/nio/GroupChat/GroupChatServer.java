package com.liu.nio.GroupChat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class GroupChatServer {

    //定义属性
    private Selector selector;
    private ServerSocketChannel listenChannel;
    private static final int PORT = 6667;

    //定义构造器
    public GroupChatServer(){
        try {
            selector = Selector.open();
            listenChannel = ServerSocketChannel.open();
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            listenChannel.configureBlocking(false);
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //监听
    public void listen(){
        try {
            while (true){
                int count = selector.select();
                if(count>0){//有事件处理
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        SelectionKey selectionKey = iterator.next();

                        //监听到连接事件
                        if(selectionKey.isAcceptable()){
                            SocketChannel socketChannel = listenChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector,SelectionKey.OP_READ);
                            System.out.println(socketChannel.getRemoteAddress()+" 上线了！");
                        }
                        //监听到读取事件
                        if(selectionKey.isReadable()){
                            //处理读（编写专门读方法）
                            readData(selectionKey);
                        }
                        //删除当前selectionKey，防止重复处理
                        iterator.remove();
                    }
                }else {
                    System.out.println("等待。。。。。。");
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {

        }
    }

    private void readData(SelectionKey key){
        SocketChannel socketChannel=null;
        try {
            socketChannel= (SocketChannel) key.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int read = socketChannel.read(byteBuffer);
            if(read>0){
                String message = new String(byteBuffer.array());
                System.out.println("from 客户端 "+message);

                //向其他用户转发消息 （构建单独的方法）
                sendInfoToOthers(message,socketChannel);
            }

        }catch (IOException e){
            try {
                System.out.println(socketChannel.getRemoteAddress() + "离线了");
                key.cancel();
                socketChannel.close();
            }catch (IOException ee){
                ee.printStackTrace();
            }
        }
    }

    private void sendInfoToOthers(String message,SocketChannel self) throws IOException{
        System.out.println("消息转发中。。。。。。");
        for (SelectionKey key : selector.keys()){
            Channel targetChannel = key.channel();
            if(targetChannel instanceof SocketChannel && targetChannel!=self){
                ((SocketChannel) targetChannel).write(ByteBuffer.wrap(message.getBytes()));
            }
        }
    }

    public static void main(String[] args) {
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }
}
