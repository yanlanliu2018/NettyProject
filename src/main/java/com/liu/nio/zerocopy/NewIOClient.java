package com.liu.nio.zerocopy;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NewIOClient {
    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost",7001));
        String fileName = "File/file01.txt";

        //得到文件的channel
        FileChannel fileChannel = new FileInputStream(fileName).getChannel();

        //准备发送
        long startTime = System.currentTimeMillis();

        //在Linux下，一个transferTo方法就可以完成传输
        //在windows中，一次调用transferTo只能发送8M的数据，因此需要分段传输文件，
        //transferTo底层使用的是0拷贝

        //8M有多少字节
        long bytecount = 1024*1024*8;
        int times  = (int)((fileChannel.size()+bytecount-1)/bytecount);
        long transferCount = 0;
        while (times>1){
            /**
             *
             参数：
             position - 文件中的位置，从此位置开始传输；必须为非负数
             count - 要传输的最大字节数；必须为非负数
             target - 目标通道
             */
            transferCount+=fileChannel.transferTo(transferCount,bytecount,socketChannel);
        }
        transferCount+=fileChannel.transferTo(transferCount,fileChannel.size()-transferCount,socketChannel);

        System.out.println("发送的总字节数："+transferCount+";耗时：" +(System.currentTimeMillis()-startTime));

        fileChannel.close();
        socketChannel.close();

    }
}
