package com.liu.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NettyByteBuf01 {
    public static void main(String[] args) {


        //创建一个 ByteBuf
        //说明
        //1. 创建对象， 该对象包含一个数组arr，是一个byte[10]
        //2. 在 netty 的 buffer中不需要进行flit 进行反转
        //  在底层维护了 readerindex 和 writeindex
        //3. 通过 readerindex 、writeindex 和 capacity 将buf 分成了三个区域
        ByteBuf buffer = Unpooled.buffer(10);

        for (int i=0;i<10;i++){
            buffer.writeByte(i);
        }

        System.out.println("capacity = " + buffer.capacity());

        //输出
//        for (int i=0;i<buffer.capacity();i++){
//            System.out.println(buffer.getByte(i));
//        }

        for (int i=0;i<buffer.capacity();i++){
            System.out.println(buffer.readByte());
        }
    }
}
