package com.spiritsword.handler;

import com.spiritsword.registry.RegistryService;
import com.spiritsword.task.model.ChannelMessage;
import com.spiritsword.task.model.ExecutorInfo;
import com.spiritsword.task.model.MessageType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

public class RegistryInboundHandler extends SimpleChannelInboundHandler<ChannelMessage> {

    private RegistryService registryService;

    private Channel schedulerChannel;

    public RegistryInboundHandler(RegistryService registryService) {
        this.registryService = registryService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ChannelMessage channelMessage) throws Exception {
        if(channelMessage.getMessageType().equals(MessageType.SCHEDULER_REGISTER)) {
            this.schedulerChannel = channelHandlerContext.channel();
        }

        if(channelMessage.getMessageType().equals(MessageType.PULL_REQUEST)) {
            List<ExecutorInfo> allAvailableExecutors = registryService.getAllAvailableExecutors();
            ChannelMessage response = new ChannelMessage();
            response.setMessageType(MessageType.EXECUTOR_LIST);
            response.setPayload(allAvailableExecutors);
            this.schedulerChannel.writeAndFlush(response);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}
