package com.example.nettyinaction.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * @author：Cheng.
 * @since：
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf)msg;
        System.out.println("收到客户端的消息："+buf.toString(CharsetUtil.UTF_8));
        solution2(ctx,msg);


        //读取客户端发送的消息
        /*
        Thread.sleep(10*1000);
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, world",CharsetUtil.UTF_8));*/

        /*
        System.out.println("服务器读取线程 "+Thread.currentThread().getName()+" channel= "+ctx.channel());
        System.out.println("server ctx= "+ctx);
        Channel channel = ctx.channel();
        //本质是一个双向链接
        ChannelPipeline pipeline = ctx.pipeline();

        ByteBuf buf = (ByteBuf) msg;
        System.out.println("客户端发送消息是："+buf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址："+channel.remoteAddress());
        */
    }

    /**
     * 读取数据，在读取数据的过程中有一个非常耗时的业务，需要服务端读取完再给客户端发送消息，则服务端的readComplete方法会一直被阻塞，
     * 如何防止其被阻塞呢？如何使读取数据过程中的耗时业务变为异步执行。
     * @param ctx
     * @param msg
     */
    public void solution1(ChannelHandlerContext ctx, Object msg){
        //用户程序自定义的普通任务
        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(5 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵2", CharsetUtil.UTF_8));
                    System.out.println("channel code=" + ctx.channel().hashCode());
                } catch (Exception ex) {
                    System.out.println("发生异常" + ex.getMessage());
                }
            }
        });

        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(2 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵3", CharsetUtil.UTF_8));
                    System.out.println("channel code=" + ctx.channel().hashCode());
                } catch (Exception ex) {
                    System.out.println("发生异常" + ex.getMessage());
                }
            }
        });
    }


    /**
     * 解决方案2： 用户自定义定时任务--->该任务是提交到scheduleTaskQueue中
     * @param ctx
     * @param msg
     */
    public void solution2(ChannelHandlerContext ctx, Object msg){
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(5 * 1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵4", CharsetUtil.UTF_8));
                    System.out.println("channel code=" + ctx.channel().hashCode());
                } catch (Exception ex) {
                    System.out.println("发生异常" + ex.getMessage());
                }
            }
        }, 5, TimeUnit.SECONDS);

        System.out.println("go on....");
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //服务端读取完毕之后，进行业务处理
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端",CharsetUtil.UTF_8));
    }

    //处理异常，一般是需要关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
