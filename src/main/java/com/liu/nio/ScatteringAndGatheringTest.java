package com.liu.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Scattering
 * Gathering:从buffer中读取数据时，可以采用buffer数组，依次读
 */
public class ScatteringAndGatheringTest{
    public static void main(String[] args) throws Exception {

        //创建通道，绑定socket并启动过端口
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);
        serverSocketChannel.socket().bind(inetSocketAddress);

        //创建buffer数组
        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        byteBuffers[0] = ByteBuffer.allocate(5);
        byteBuffers[1] = ByteBuffer.allocate(3);

        //等待客户端连接
        SocketChannel socketChannel = serverSocketChannel.accept();
        int messageLength = Arrays.asList(byteBuffers).stream().map(byteBuffer -> byteBuffer.limit()).reduce(0,(x,y)->x+y);
        System.out.println("messageLength = "+messageLength);

        //循环读取
        while (true){
            int byteRead = 0;
            while (byteRead<messageLength){
                long l = socketChannel.read(byteBuffers);
                byteRead+=l;
                System.out.println("byteRead = "+byteRead);
                Arrays.asList(byteBuffers).stream().map(byteBuffer -> "position = "+byteBuffer.position()
                        +";limit = "+byteBuffer.limit()).forEach(System.out::println);
            }

            Arrays.asList(byteBuffers).forEach(byteBuffer -> byteBuffer.flip());

            //将数据显示到客户端
            int byteWrite = 0;
            while (byteWrite<messageLength){
                long l = socketChannel.write(byteBuffers);
                byteWrite+=l;
            }

            //将所有到byteBuffer进行clear
            Arrays.asList(byteBuffers).forEach(byteBuffer -> byteBuffer.clear());

            System.out.println("byteRead = "+byteRead+";byteWrite = "+byteWrite+";messageLength = "+messageLength);
        }
    }
}
