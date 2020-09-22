package com.liu.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOServer {
    public static void main(String[] args) throws IOException {
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        ServerSocket serverSocket = new ServerSocket(666);
        System.out.println("服务器启动了");
        while (true){
            final Socket socket = serverSocket.accept();
            newCachedThreadPool.execute(new Runnable() {
                public void run() {
                    handle(socket);
                }
            });
        }
    }

    public static void handle(Socket socket){
        try {
            System.out.println("线程信息： id="+Thread.currentThread().getId()+
                    ";name = "+Thread.currentThread().getName()+"。");
            byte[] bytes = new byte[1024];
            InputStream inputStream = socket.getInputStream();
            while (true){
                System.out.println("线程信息： id="+Thread.currentThread().getId()+
                        ";name = "+Thread.currentThread().getName()+"。");
                int read = inputStream.read(bytes);
                if(read!=-1){
                    System.out.println(new String(bytes,0,read));
                }else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println("关闭与client的连接");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
