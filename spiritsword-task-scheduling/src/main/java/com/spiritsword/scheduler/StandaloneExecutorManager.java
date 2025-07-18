package com.spiritsword.scheduler;

import com.spiritsword.task.model.ChannelMessage;
import com.spiritsword.task.model.ExecutorInfo;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StandaloneExecutorManager implements ExecutorManager {
    private static final Logger log = LoggerFactory.getLogger(StandaloneExecutorManager.class.getName());
    private Map<String, Channel> executors = new ConcurrentHashMap<>();
    private List<ExecutorInfo> executorInfos = new ArrayList<>();

    @Override
    public void updateExecutorInfos(List<ExecutorInfo> executorInfos) {
//        if(!executorInfos.isEmpty()) {
//            validateExecutorInfos(executorInfos);
//        } else {
//
//        }
        this.executorInfos = executorInfos;
    }

    @Override
    public void validateExecutorInfos(List<ExecutorInfo> executorInfos) {

    }

    @Override
    public void registerExecutor(ChannelMessage channelMessage, Channel channel) {
        log.info("Channel registered on Scheduler");
        this.executors.put(channelMessage.getExecutorId(), channel);
        log.info("Number of channels registered on scheduler: {}", this.executors.size() );
    }

    @Override
    public void removeExecutor(String executorId) {
        executors.remove(executorId);
    }

    @Override
    public ExecutorInfo getExecutorInfo(String executorId) {
        return executorInfos.stream().filter(e -> e.getExecutorId().equals(executorId)).findFirst().orElse(null);
    }

    @Override
    public ExecutorInfo selectExecutorInfoByType(String executorType, String handlerClassName) {
        for(ExecutorInfo executorInfo : executorInfos) {
            String executorTaskType = executorInfo.getExecutorTaskType();
            if(executorTaskType.equals(executorType) && executorInfo.supports(handlerClassName)) {
                return executorInfo;
            }
        }
        return null;
    }

    @Override
    public void dispatch(String executorId, ChannelMessage channelMessage) {
        //检查处理
        Channel channel = executors.get(executorId);
        channel.writeAndFlush(channelMessage);
    }
}
