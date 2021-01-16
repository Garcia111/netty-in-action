package com.example.nettyinaction.nio;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * scattering: 将数据写入到buffer时，可以采用buffer数组，依次写入
 * Gathering: 从buffer读取数据时，可以采用buffer数组，依次读取
* @author：Cheng.
* @since： 
 */public class ScatteringAndGatheringTest {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);

        //绑定端口到ServerSocketChannel,并启动
        serverSocketChannel.socket().bind(inetSocketAddress);

        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        byteBuffers[0] = ByteBuffer.allocate(5);
        byteBuffers[1] = ByteBuffer.allocate(3);

        //服务端监听连接
        System.out.println("wait.....");
        SocketChannel socketChannel = serverSocketChannel.accept();

        int maxMessageLength = 8;

        while (true){
            int byteRead = 0;

            while (byteRead < maxMessageLength){
                //将客户端传送的内容写入到ByteBuffer

                long read =  socketChannel.read(byteBuffers);
                byteRead += read;
                System.out.println("read="+read);
                Arrays.stream(byteBuffers).map(byteBuffer -> "position="+ byteBuffer.position()
                +", limit="+byteBuffer.limit()).forEach(System.out::println);
            }
            //将所有的ByteBuffer进行反转
            Arrays.stream(byteBuffers).forEach(byteBuffer -> byteBuffer.flip());

            //将读入到ByteBuffer中的数据打印输出
            long byteWrite = 0;
            while (byteWrite < maxMessageLength){
                long write = socketChannel.write(byteBuffers);
                byteWrite += write;
            }

            Arrays.stream(byteBuffers).forEach(byteBuffer -> byteBuffer.flip());
            System.out.println("byteRead="+byteRead+", byteWrite="+byteWrite+", messageLength="+maxMessageLength);
        }
    }




}
