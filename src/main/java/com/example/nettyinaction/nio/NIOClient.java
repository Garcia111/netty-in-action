package com.example.nettyinaction.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author：Cheng.
 * @since：
 */
public class NIOClient {

    public static void main(String[] args) throws IOException {
        //1.得到一个SocketChannel
        //2. 设置SocketChannel为非阻塞
        //提供server端的ip和端口
        //连接服务器
        //将socketChannel中的数据写入到一个ByteBuffer中

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.01",6666);
        if(!socketChannel.connect(inetSocketAddress)){
            //如果没有连接到服务器
            while (!socketChannel.finishConnect()){
                //一直没有结束连接，即客户端虽然没有连接上，但是一直在尝试连接，在这段时间内，客户端不会被阻塞，可以去做其他操作
                System.out.println("尝试连接中，可以进行其他业务操作");
            }
        }

        System.out.println("连接成功");
        String str = "坚持住呀，胜利就在前方了";
        ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes());
        //将数据从Buffer中写入通道
        socketChannel.write(byteBuffer);
        System.in.read();
    }

}
