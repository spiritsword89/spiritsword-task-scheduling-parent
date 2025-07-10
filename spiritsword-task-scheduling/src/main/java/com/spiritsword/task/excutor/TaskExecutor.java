package com.spiritsword.task.excutor;

import com.spiritsword.task.handler.TaskHandler;
import com.spiritsword.task.model.ChannelMessage;
import io.netty.channel.Channel;

public interface TaskExecutor {
    public String getExecutorId();
    public void execute(ChannelMessage channelMessage);
    public boolean supports(String handlerClass);
    public double getLoad();
    public boolean isHealthy();
    public void registerHandler(String handlerId, TaskHandler handler);
    public void removeHandler(String handlerId);
    public void reconnectRegistry();
    public void reconnectScheduler();
}
