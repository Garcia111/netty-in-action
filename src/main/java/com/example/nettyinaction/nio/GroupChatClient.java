package com.example.nettyinaction.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * @author：Cheng.
 * @since：
 */
public class GroupChatClient {
    private final String serverHost = "127.0.0.1";
    private final Integer serverPort = 6667;
    private SocketChannel socketChannel;
    //client要注册到server的selector
    private Selector selector;
    private String userName;

    public GroupChatClient() throws IOException {
        socketChannel = SocketChannel.open(new InetSocketAddress(serverHost, serverPort));
        selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        userName = socketChannel.getLocalAddress().toString().substring(1);
        System.out.println(userName + "is ok.....");
    }

    //客户端要连接服务器
    //客户端要向服务器发送消息
    public void send(String msg) {

        msg = userName + " 说：" + msg;
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
            //将ByteBuffer中的数据输出到Channel中
            socketChannel.write(byteBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    //从服务器接收消息
    public void receiveMessage(){
        try{
            int readChannels = selector.select();
            if(readChannels >0){
                Set<SelectionKey> keySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keySet.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();

                    if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel)key.channel();
                        //将channel中的数据读取到ByteBuffer中
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        socketChannel.read(byteBuffer)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               ;
                        System.out.println(new String(byteBuffer.array()));
                    }
                    //删除已经处理过的SelectionKey 防止重复处理
                    iterator.remove();
                }
            }else{
                System.out.println("没有可用的Channel");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        GroupChatClient client = new GroupChatClient();
        //每隔3秒从服务端收听下消息
        new Thread(()->{
            client.receiveMessage();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            String msg = scanner.nextLine();
            client.send(msg);
        }
    }
}
