package com.liu.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

public class NettyByteBuf02 {
    public static void main(String[] args) {

        //创建 buffer
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello,world!", Charset.forName("utf-8"));

        //使用相关的方法
        if (byteBuf.hasArray()){
            byte[] content = byteBuf.array();

            //将 content 转成字符串
            System.out.println(new String(content,Charset.forName("utf-8")));

            System.out.println("byteBuf  = "+ byteBuf);

            System.out.println(byteBuf.arrayOffset()); //0
            System.out.println(byteBuf.readerIndex()); //0
            System.out.println(byteBuf.writerIndex()); //12
            System.out.println(byteBuf.capacity()); //36

            System.out.println(byteBuf.readByte());

            int len = byteBuf.readableBytes(); // 可读取的字节数 12
            System.out.println(len);

            for (int i=0;i<len;i++){
                System.out.println((char) byteBuf.getByte(i));
            }

            // 按照某个范围读取
            System.out.println(byteBuf.getCharSequence(0,4,Charset.forName("utf-8")));
        }
    }
}
