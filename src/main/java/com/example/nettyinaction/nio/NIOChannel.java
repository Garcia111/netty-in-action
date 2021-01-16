package com.example.nettyinaction.nio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author：Cheng.
 * @since：
 */
public class NIOChannel {

    public static void main(String[] args) throws IOException {
        //将hello world通过FileChannel 和 ByteBuffer 写入到文件file.txt中
        //1. 获取Java原生的FileOutputStream
        FileOutputStream fileOutputStream = new FileOutputStream("d:\\file.txt");
        //使用FileChannel对FileOutputStream包一层
        FileChannel fileChannel = fileOutputStream.getChannel();
        //将字符串放入ByteBuffer中
        String str = "hello world!";
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(str.getBytes());

        //上面是byteBuffer读，下面需要byteBuffer写，所以需要对byteBuffer进行切换
        byteBuffer.flip();

        //将ByteBuffer中的数据写出到FileChannel中
        fileChannel.write(byteBuffer);

        //关闭FileOutputStream
        fileOutputStream.close();

    }
}
