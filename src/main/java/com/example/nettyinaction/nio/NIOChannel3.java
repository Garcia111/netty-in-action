package com.example.nettyinaction.nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
* @author：Cheng.
* @since： 
 */public class NIOChannel3 {

    public static void main(String[] args) throws IOException {
//        File file1 = new File("NOChannel.txt");
        FileInputStream fileInputStream = new FileInputStream("src/NOChannel.txt");
        FileChannel fileChannel1 = fileInputStream.getChannel();

        File file2 = new File("d:\\file.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(file2);
        FileChannel fileChannel2 = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        while (true){
            byteBuffer.clear();
            int read = fileChannel1.read(byteBuffer);
            if(read != -1){
                byteBuffer.flip();
                fileChannel2.write(byteBuffer);
            }else{
                break;
            }

        }
    }
}
