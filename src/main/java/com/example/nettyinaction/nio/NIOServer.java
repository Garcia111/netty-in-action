package com.example.nettyinaction.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * @author：Cheng.
 * @since：
 */
public class NIOServer {

    public static void main(String[] args) throws IOException {

        //创建ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //创建一个Selector对象
        Selector selector = Selector.open();
        //为SocketChannel绑定一个端口
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        //将ServerSocketChannel注册到Selector
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //循环等待客户端连接
        while (true){
            if(selector.select(1000)==0){
                //等待一秒钟，仍然没有连接，就继续
                System.out.println("一直没有连接");
                continue;
            }

            //如果有连接且有事件，就获取到相关的SelectionKey集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            System.out.println("selectionKeys 数量 = " + selectionKeys.size());

            Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();
            while (selectionKeyIterator.hasNext()){
                SelectionKey selectionKey = selectionKeyIterator.next();
                //根据Key对应的通道发生的时间做相应处理
                //如果事件为连接事件，通过ServerSocketChannel获取一个SocketChannel
                if(selectionKey.isAcceptable()){
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("客户端连接成功 生成了一个 socketChannel " + socketChannel.hashCode());
                    //将SocketChannel设置为非阻塞，同样将该SocketChannel注册到Selector，并且为该SocketChannel绑定一个ByteBuffer
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(512));
                    System.out.println("客户端连接后 ，注册的selectionkey 数量=" + selector.keys().size()); //2,3,4..
                }
                if(selectionKey.isReadable()){
                    //如果发生事件为读取事件，通过SelectionKey反向获取到对应的Channel,获取到该Channel关联的Buffer,将该Channel中的数据读取到Buffer中
                    SocketChannel selectChannel = (SocketChannel)selectionKey.channel();
                    ByteBuffer byteBuffer = (ByteBuffer)selectionKey.attachment();
                    selectChannel.read(byteBuffer);
                    System.out.println("从客户端获取信息："+ new String(byteBuffer.array()));
                }
                //在操作完SelectionKey之后将其删除，防止重复处理
                selectionKeyIterator.remove();
            }
        }

    }
}
