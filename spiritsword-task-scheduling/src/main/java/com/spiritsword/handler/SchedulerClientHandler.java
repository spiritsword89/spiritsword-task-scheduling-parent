package com.spiritsword.handler;

import com.alibaba.fastjson.JSON;
import com.spiritsword.scheduler.StandaloneExecutorManager;
import com.spiritsword.task.model.ChannelMessage;
import com.spiritsword.task.model.ExecutorInfo;
import com.spiritsword.task.model.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 *  接收注册中心的拉取列表
 */
public class SchedulerClientHandler extends SimpleChannelInboundHandler<ChannelMessage> {

    private StandaloneExecutorManager executorManager;

    public SchedulerClientHandler(StandaloneExecutorManager executorManager) {
        this.executorManager = executorManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ChannelMessage channelMessage) throws Exception {
        if(channelMessage.getMessageType().equals(MessageType.EXECUTOR_LIST)) {
            this.executorManager.updateExecutorInfos(JSON.parseArray(JSON.toJSONString(channelMessage.getPayload()), ExecutorInfo.class));
        }
    }
}
