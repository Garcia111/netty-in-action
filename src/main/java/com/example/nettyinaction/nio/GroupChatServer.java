package com.example.nettyinaction.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 编写一个 NIO 群聊系统，实现服务器端和客户端之间的数据简单通讯（非阻塞）
 * 实现多人群聊
 * 服务器端：可以监测用户上线，离线，并实现消息转发功能
 * 客户端：通过channel 可以无阻塞发送消息给其它所有用户，同时可以接受其它用户发送的消息(有服务器转发得到)
 * 目的：进一步理解NIO非阻塞网络编程机制
 *
 * @author：Cheng.
 * @since：
 */
public class GroupChatServer {
    private ServerSocketChannel listenChannel;
    private static final Integer PORT = 6667;
    private Selector selector;

    public GroupChatServer() throws IOException {
        this.listenChannel = ServerSocketChannel.open();
        this.selector = Selector.open();
        listenChannel.socket().bind(new InetSocketAddress(PORT));

        //需要将selector与listenChannel进行绑定
        //设置非阻塞模式必须要在注册之前执行
        listenChannel.configureBlocking(false);
        listenChannel.register(selector,SelectionKey.OP_ACCEPT);

    }

    //1.监听客户端连接
    public void listen(){
        System.out.println("监听线程： "+Thread.currentThread().getName());
        try{
            while (true){
                int count = selector.select();
                if(count > 0){
                    //获取已连接的SocketChannel
                    Set<SelectionKey> keySet = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = keySet.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        if (key.isAcceptable()) {
                            SocketChannel socketChannel = listenChannel.accept();
                            //设置为非阻塞模式
                            socketChannel.configureBlocking(false);
                            //将socketChannel注册到selector
                            socketChannel.register(selector, SelectionKey.OP_READ);

                            System.out.println(socketChannel.getRemoteAddress() + " 上线 ");
                        }

                        if (key.isReadable()) {
                            //读取client发送的消息
                            read(key);
                        }
                        //删除已经处理过的SelectionKey 防止重复处理
                        iterator.remove();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * 获取Client消息
     */
    public void read(SelectionKey selectionKey) throws IOException {
        SocketChannel channel = null;
        //获取selectionKey对应的Channel
        try {
            channel = (SocketChannel) selectionKey.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            //从channel中读取数据到ByteBuffer
            int count = channel.read(byteBuffer);
            if (count > 0) {
                //获取到client发送的消息
                String message = new String(byteBuffer.array());
                System.out.println("收到来自"+channel.getRemoteAddress()+"的消息："+message);
                //转发消息
                transport(message,channel);
            }
        } catch (Exception e) {
            try {
                System.out.println(channel.getRemoteAddress() + "离线了。。。");
                //关闭key  channel
                selectionKey.cancel();
                channel.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

    }


    //3. 转发Client消息

    /**
     * 转发client消息：消息 除了自己之外的其他通道
     * 可以通过selector获取所有的channel, 只要排除消息发送channel即可
     */
    public void transport(String msg, SocketChannel socketChannel) throws IOException {

        System.out.println("服务器转发消息中......");
        System.out.println("服务器转发数据给客户端线程："+Thread.currentThread().getName());
        for(SelectionKey key : selector.keys()){
            Channel targetChannel = key.channel();
            if(targetChannel != socketChannel && targetChannel instanceof SocketChannel){
                //将消息转发给其他的channel
                ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
                //将ByteBuffer中的数据写入到SocketChannel
                ((SocketChannel) targetChannel).write(byteBuffer);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        GroupChatServer server = new GroupChatServer();
        server.listen();
    }

}
