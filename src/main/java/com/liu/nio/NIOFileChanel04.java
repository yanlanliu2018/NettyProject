package com.liu.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class NIOFileChanel04 {
    public static void main(String[] args) throws Exception {
        FileInputStream inputStream = new FileInputStream("File/a.jpg");
        FileOutputStream outputStream = new FileOutputStream("File/b.jpg");

        FileChannel inputStreamChannel = inputStream.getChannel();
        FileChannel outputStreamChannel = outputStream.getChannel();

        outputStreamChannel.transferFrom(inputStreamChannel,0,inputStreamChannel.size());

        inputStream.close();
        inputStreamChannel.close();
        outputStream.close();
        outputStreamChannel.close();
    }
}

