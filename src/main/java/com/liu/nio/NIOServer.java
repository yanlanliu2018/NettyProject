package com.liu.nio;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

// 死循环的原因：https://zhuanlan.zhihu.com/p/92133508
public class NIOServer {
    public static void main(String[] args) throws Exception {

        // 创建serverSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 得到一个selector对象
        Selector selector = Selector.open();
        // 绑定一个端口6666,在服务器端监听
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        // 设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        // 将serverSocketChannel注册到selector，关心事件为OP_ACCEPT（连接事件）
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            // 这里我们等待一秒，如果没有事件发生，就继续
            if (selector.select(3000) == 0){ // 没有事件发生
                System.out.println("服务器等待中，无连接");
                continue;
            }

            // 如果返回的大于0，表示已经获取到关注的事件了，获取selectionKey的集合
            // 可以反向获取通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            // 遍历selectKeySet
            while (keyIterator.hasNext()) {
                SelectionKey selectionKey = keyIterator.next();
                // 监听1:查看这个key对应的通道发生的关注的事件，这里我们关注serverSocketChannel的连接事件
                if (selectionKey.isAcceptable() && selectionKey.channel() instanceof ServerSocketChannel) {
                    // 有新客户端连接（OP_ACCEPT），给该客户端生成socketChannel
                    /**
                     * accept方法为阻塞方法！但是那是在连接未发生时的阻塞，
                     * 因为selectionKey.isAcceptable()以及判断出了连接的产生，
                     * 所有accept()不会阻塞，会立刻执行
                     */
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("客户端连接成功，生成一个socketChannel:"+socketChannel.hashCode());
                    socketChannel.configureBlocking(false);
                    // 将socketChannel注册到selector，关注事件为就绪读（channel中已经产生数据），
                    // 同时给channel关联buffer
                    socketChannel.register(selector, SelectionKey.OP_READ);

                }
                // 监听2:如果socketchannel已经有数据等待读取
                else if (selectionKey.isReadable() && selectionKey.channel() instanceof SocketChannel) {
                    // 产生就绪读事件（OP_READ）
                    SocketChannel channel = (SocketChannel) selectionKey.channel();
                    // 获取到该channel关联的buffer
                    ByteBuffer buffer = ByteBuffer.allocate(10);
                    // 读取通道数据到buffer
                    int read = channel.read(buffer);
                    if (read <= 0){
                        /**
                         * 不调用这一句会在取消连接时死循环，因为selector仍能检测到该key
                         */
                        selectionKey.cancel();
                        System.out.println("未读到数据，取消selectionKey");
                    }else {
                        System.out.println("from client:" + new String(buffer.array()));
                    }
                }
                // 手动从集合中移除，否则会出现重复操作
                keyIterator.remove();
            }
        }
    }
}
