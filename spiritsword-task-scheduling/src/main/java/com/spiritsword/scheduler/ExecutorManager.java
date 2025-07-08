package com.spiritsword.scheduler;

import com.spiritsword.task.model.ChannelMessage;
import com.spiritsword.task.model.ExecutorInfo;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExecutorManager {
    private Map<String, Channel> executors = new ConcurrentHashMap<>();
    private List<ExecutorInfo> executorInfos = new ArrayList<>();

    public void updateExecutorInfos(List<ExecutorInfo> executorInfos) {
        if(!executorInfos.isEmpty()) {
            validateExecutorInfos(executorInfos);
        } else {
            this.executorInfos = executorInfos;
        }
    }

    private void validateExecutorInfos(List<ExecutorInfo> executorInfos) {

    }

    public void registerExecutor(ChannelMessage channelMessage, Channel channel) {
        executors.put(channelMessage.getExecutorId(), channel);
    }

    public void removeExecutor(String executorId) {
        executors.remove(executorId);
    }

    public ExecutorInfo getExecutorInfo(String executorId) {
        return executorInfos.stream().filter(e -> e.getExecutorId().equals(executorId)).findFirst().orElse(null);
    }


}
