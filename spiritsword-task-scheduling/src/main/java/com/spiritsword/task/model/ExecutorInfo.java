package com.spiritsword.task.model;

import java.io.Serializable;
import java.util.List;

public class ExecutorInfo implements Serializable {
    private String executorId;
    private String executorTaskType;
    private List<String> handlerClassList;
    private String host;
    private int port;
    private long lastHeartbeat;

    public boolean isAvailable() {
        return lastHeartbeat + 5 * 60 * 1000 > System.currentTimeMillis();
    }

    public String getExecutorId() {
        return executorId;
    }

    public void setExecutorId(String executorId) {
        this.executorId = executorId;
    }

    public String getExecutorTaskType() {
        return executorTaskType;
    }

    public void setExecutorTaskType(String executorTaskType) {
        this.executorTaskType = executorTaskType;
    }

    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<String> getHandlerClassList() {
        return handlerClassList;
    }

    public void setHandlerClassList(List<String> handlerClassList) {
        this.handlerClassList = handlerClassList;
    }

    public boolean supports(String handlerClassName) {
        return this.handlerClassList.contains(handlerClassName) && isAvailable();
    }
}
