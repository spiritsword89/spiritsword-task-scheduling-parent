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
    public Channel channel();
    public void registerHandler(TaskHandler handler);
    public void removeHandler(String handlerId);
    public void reconnect();
}
