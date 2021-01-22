package com.example.nettyinaction.netty.handlerscheme.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * decode会根据接收的数据，被多次调用，知道确定没有新的元素被添加的list,或者是ByteBuf没有更多的可读字节位置
 * 如果list out不为空，就将list的内容传递给下一个channelInBoundHandler处理，该处理器的方法也会被调用多次
 *
 * @author：Cheng.
 * @since：
 */
public class MyByteToLongDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("MyByteToLongDecoder 被调用");
        //因为long为8个字节，需要判断有8个字节，才能读取一个long,将数据读取出来，然后添加到Object中
        if(in.readableBytes() >= 8){
            out.add(in.readLong());
        }

    }
}
