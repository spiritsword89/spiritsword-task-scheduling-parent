package com.spiritsword.task.model;

import java.io.Serializable;
import java.util.List;

public class ExecutorInfo implements Serializable {
    private String executorId;
    private String executorType;
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

    public String getExecutorType() {
        return executorType;
    }

    public void setExecutorType(String executorType) {
        this.executorType = executorType;
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
}
