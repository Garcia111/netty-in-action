package com.example.nettyinaction.netty.simple;

import io.netty.bootstrap.BootstrapConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author：Cheng.
 * @since：
 */
public class NettyServer {

    public static void main(String[] args) throws Exception{

        //创建BossGroup 和 WorkerGroup

        //1.创建两个线程组 bossGroup 和 workerGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);

        //2. bossGroup 只是处理连接请求，真正的和客户端进行业务处理会交给workerGroup
        //3. NioEventLoop 都是无限循环
        //4. bossGroup 和 workerGroup 含有的子线程NioEventLoop的个数 默认为cpu核数 *2


        //创建服务器端的启动对象，配置参数，使用链式编程来进行设置，将两个线程组作为启动参数进行设置
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    //设置服务器端的通道 NioServerSocketChannel
                    .channel(NioServerSocketChannel.class)
                    //设置可以连接的client个数
                    .option(ChannelOption.SO_BACKLOG,128)
                    //设置连接保持活动状态
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    //对Channel进行初始化设置，设置channel对应的pipeline 和 pipeline中处理业务的Handler
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //设置一个通道初始化对象 对Channel进行初始化设置，设置channel对应的pipeline 和 pipeline中的NettyServerHandler
                            System.out.println("客户socketChannel hashCode= "+ ch.hashCode());
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });

            System.out.println("......服务器 is ready......");

            ChannelFuture cf = serverBootstrap.bind(6668).sync();

            //给channel注册监听器，监控关心的事件
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(cf.isSuccess()){
                        System.out.println("监听端口 6668成功");
                    }else{
                        System.out.println("监听端口与 6668失败");
                    }
                }
            });

            //对关闭通道进行监听
            cf.channel().closeFuture().sync();

        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
