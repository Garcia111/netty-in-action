package com.example.nettyinaction.nio;

import java.nio.IntBuffer;

/**
 * @author：Cheng.
 * @since：
 */
public class BufferTest {

    public static void main(String[] args) {
        IntBuffer intBuffer = IntBuffer.allocate(5);

        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i * 2);
        }

        //intBuffer切换为读
        intBuffer.flip();

        while (intBuffer.capacity()>0){
            System.out.println(intBuffer.get());
        }

    }
}
