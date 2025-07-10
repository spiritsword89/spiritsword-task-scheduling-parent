package com.spiritsword.handler;

import com.spiritsword.task.model.ChannelMessage;
import com.spiritsword.task.model.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SchedulerServerHandler extends SimpleChannelInboundHandler<ChannelMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ChannelMessage channelMessage) throws Exception {
        if(channelMessage.getMessageType().equals(MessageType.EXECUTOR_REGISTER)) {

        }

        if(channelMessage.getMessageType().equals(MessageType.TASK_RESPONSE)) {

        }
    }
}
