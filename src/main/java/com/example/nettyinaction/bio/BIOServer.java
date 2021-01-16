package com.example.nettyinaction.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author：Cheng.
 * @since：
 */
public class BIOServer {

    public static void main(String[] args) {

        //思路
        //1. 创建一个线程池
        //2. 如果有客户端连接，就创建一个线程与其通讯（单独写一个方法）


        try {
            ExecutorService executorService = Executors.newCachedThreadPool();
            ServerSocket serverSocket = new ServerSocket(6666);
            System.out.println("服务启动了");

            while (true){
                System.out.println("等待监听连接......");
                Socket socket = serverSocket.accept();
                System.out.println("连接到一个客户端");
                executorService.execute(()->{
                   //与客户端进行通讯
                    handleSocket(socket);
                });
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public static void handleSocket(Socket socket){
        byte[] bytes = new byte[1024];
        System.out.println(Thread.currentThread().getName());
        InputStream inputStream = null;
        try {
            inputStream =  socket.getInputStream();

            while (true){
                System.out.println("read......");
                int length = inputStream.read(bytes);

                if (length != -1){
                    System.out.println(new String(bytes,0,length));
                }else{
                    break;
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
