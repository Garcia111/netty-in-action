package com.example.nettyinaction.netty.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author：Cheng.
 * @since：
 */
public class MyServerHandler extends ChannelInboundHandlerAdapter {


    /**
     * 空闲时间触发此Handler中的方法
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            //判断是否为空闲事件类型
            IdleStateEvent event = (IdleStateEvent)evt;
            String eventType = null;
            switch(event.state()){
                case READER_IDLE://读空闲事件
                    eventType = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;
            }
            System.out.println(ctx.channel().remoteAddress()+"--超时事件--"+eventType);
            System.out.println("服务器做相应处理...");

        }
    }
}
