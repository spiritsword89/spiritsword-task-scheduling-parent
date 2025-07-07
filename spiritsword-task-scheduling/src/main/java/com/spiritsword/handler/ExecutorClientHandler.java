package com.spiritsword.handler;

import com.spiritsword.task.model.ChannelMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ExecutorClientHandler extends SimpleChannelInboundHandler<ChannelMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ChannelMessage channelMessage) throws Exception {

    }
}
