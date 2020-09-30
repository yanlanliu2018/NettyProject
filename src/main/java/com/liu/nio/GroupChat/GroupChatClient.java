package com.liu.nio.GroupChat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class GroupChatClient {

    //定义属性
    private final String HOST = "127.0.0.1";
    private final int PORT = 6667;
    private Selector selector;
    private SocketChannel socketChannel;
    private String username;

    public GroupChatClient() throws Exception {
        this.selector = Selector.open();
        this.socketChannel = SocketChannel.open(new InetSocketAddress(HOST,PORT));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        username = socketChannel.getLocalAddress().toString().substring(1);
        System.out.println(username + "is ok...");
        sendInfo("我上线啦！！！！");
    }

    //向服务器发送消息
    public void sendInfo(String info){
        info = username + "说："+info;
        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取服务端回复的消息
    public void readInfo(){
        try {
            int readChannels = selector.select();
            if(readChannels>0){
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if(key.isReadable()){
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        socketChannel.read(byteBuffer);
                        String message = new String(byteBuffer.array());
                        System.out.println(message.trim());
                    }
                }
                //删除当前的selectionKey, 防止重复操作
                //在实验时，忘记写该语句导致出现的问题：
                //客户端在第一次接收语句之后，再无法接收其他客户端发送对语句。
                iterator.remove();
            }else {

            }
        }catch (Exception e){

        }
    }

    public static void main(String[] args) throws Exception {

        //创建客户端实例
        GroupChatClient groupChatClient = new GroupChatClient();

        //启动客户端线程
        new Thread(){
            @Override
            public void run() {
                while (true){
                    groupChatClient.readInfo();
                    try {
                        Thread.currentThread().sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        //发送数据给服务端
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            String info = scanner.nextLine();
//            System.out.println(info);
            groupChatClient.sendInfo(info);
        }
    }
}
