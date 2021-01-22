package com.example.nettyinaction.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 编写一个Netty心跳检测机制，
 * 当服务器超过3秒没有读，就提示读空闲
 * 当服务器超过5秒没有写，就提示写空闲
 * 当服务器超过7秒没有读或者写时，就提示读写空闲
 * @author：Cheng.
 * @since：
 */
public class MyServer {
    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            /*加入一个netty提供的IdleStateHandler
                               IdleStateHandler 是netty提供的处理空闲状态的处理器
                               1. long readIdleTime: 表示多长时间没有读，就会发送一个心跳检测包检测是否连接
                               2. long writeIdleTime: 表示多长时间没有写，就会发送一个心跳检测包检测是否连接
                               3. long allIdleTime: 表示多长时间没有读写，就会发送一个心跳检测包检测是否连接
                               当IdleStateEvent触发之后，就会传递给管道中的下一个handler去处理，
                               通过调用下一个handler的 userEventTriggered， 在该方法中去处理IdleStateEvent(读空闲 写空闲  读写空闲)

                            */
                            pipeline.addLast(new IdleStateHandler(4,2,10, TimeUnit.SECONDS));
                            //加入一个对空闲检测进一步处理的handler
                            pipeline.addLast(new MyServerHandler());
                        }
                    });

            //启动服务器
            ChannelFuture channelFuture = serverBootstrap.bind(7000).sync();
            channelFuture.channel().closeFuture().sync();


        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
