package com.example.nettyinaction.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author：Cheng.
 * @since：
 */
public class NIOChannel2 {

    public static void main(String[] args) throws IOException {
        //读取文件中的数据进行输出
        File file = new File("d:\\file.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        //获取FileChannel
        FileChannel fileChannel = fileInputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate((int)file.length());
        //将fileChannel中的数据读取到ByteBuffer中
        fileChannel.read(byteBuffer);
        //将buffer进行读写切换
        byteBuffer.flip();
        //byteBuffer获取缓冲区中的数组
        System.out.println(new String(byteBuffer.array()));
    }
}
