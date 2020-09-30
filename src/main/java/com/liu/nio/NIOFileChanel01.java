package com.liu.nio;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChanel01 {
    public static void main(String[] args) throws Exception{
        String str = "hello word!";

        FileOutputStream fileOutputStream = new FileOutputStream("/Users/zhangdeng/IdeaProjects/NettyProject/File/file01.txt");

        FileChannel channel = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        byteBuffer.put(str.getBytes());

        byteBuffer.flip();

        channel.write(byteBuffer);

        fileOutputStream.close();

    }
}
