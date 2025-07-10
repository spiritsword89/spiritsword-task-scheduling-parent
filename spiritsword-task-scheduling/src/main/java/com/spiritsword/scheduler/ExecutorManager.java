package com.spiritsword.scheduler;

import com.spiritsword.task.model.ChannelMessage;
import com.spiritsword.task.model.ExecutorInfo;
import io.netty.channel.Channel;

import java.util.List;

public interface ExecutorManager {
    public void updateExecutorInfos(List<ExecutorInfo> executorInfos);
    public void validateExecutorInfos(List<ExecutorInfo> executorInfos);
    public void registerExecutor(ChannelMessage channelMessage, Channel channel);
    public void removeExecutor(String executorId);
    public ExecutorInfo getExecutorInfo(String executorId);
}
