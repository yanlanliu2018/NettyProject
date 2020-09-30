package com.liu.nio;

import java.nio.IntBuffer;

public class BasicBuffer {
    public static void main(String[] args) {
        //简单举例说明buffer的使用

        //可以存放5个int的buffer
        IntBuffer intBuffer = IntBuffer.allocate(5);

        //向buffer中存放数据
        for (int i=0;i<intBuffer.capacity();i++){
            intBuffer.put(10+i);
        }

        //读取数据
        //读写转换
        intBuffer.flip();
        while (intBuffer.hasRemaining()){
            System.out.println(intBuffer.get());
        }
    }
}
