package com.spiritsword.handler;

import com.alibaba.fastjson2.JSON;
import com.spiritsword.scheduler.ExecutorManager;
import com.spiritsword.scheduler.ResponseProcessor;
import com.spiritsword.scheduler.ResponseProcessorChain;
import com.spiritsword.task.model.ChannelMessage;
import com.spiritsword.task.model.MessageType;
import com.spiritsword.task.model.TaskResult;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SchedulerServerHandler extends SimpleChannelInboundHandler<ChannelMessage> {

    private ExecutorManager executorManager;

    private ResponseProcessorChain responseProcessorChain;

    public  SchedulerServerHandler(ExecutorManager executorManager,  ResponseProcessorChain responseProcessorChain) {
        this.executorManager = executorManager;
        this.responseProcessorChain = responseProcessorChain;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ChannelMessage channelMessage) throws Exception {
        if(channelMessage.getMessageType().equals(MessageType.EXECUTOR_REGISTER)) {
            executorManager.registerExecutor(channelMessage, channelHandlerContext.channel());
        }

        if(channelMessage.getMessageType().equals(MessageType.TASK_RESPONSE)) {
            TaskResult taskResult = JSON.parseObject(JSON.toJSONString(channelMessage.getPayload()), TaskResult.class);
            responseProcessorChain.doProcess(taskResult);
        }
    }
}
