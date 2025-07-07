package com.spiritsword.handler;

import com.spiritsword.task.excutor.TaskExecutor;
import com.spiritsword.task.model.ChannelMessage;
import com.spiritsword.task.model.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ExecutorClientHandler extends SimpleChannelInboundHandler<ChannelMessage> {

    private TaskExecutor taskExecutor;

    public ExecutorClientHandler(TaskExecutor taskExecutor) {
        this.taskExecutor =  taskExecutor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ChannelMessage channelMessage) throws Exception {
        if(channelMessage.getMessageType().equals(MessageType.TASK_REQUEST)) {
            taskExecutor.execute(channelMessage);
        }
    }
}
