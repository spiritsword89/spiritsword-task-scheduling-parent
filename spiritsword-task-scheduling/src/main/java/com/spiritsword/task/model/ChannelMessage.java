package com.spiritsword.task.model;

public class ChannelMessage {
    private MessageType messageType;
    private String executorId;
    private String taskId;
    private Object payload;

    public ChannelMessage(MessageType messageType, String clientId, String taskId, Object payload) {
        this.messageType = messageType;
        this.executorId = clientId;
        this.taskId = taskId;
        this.payload = payload;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getExecutorId() {
        return executorId;
    }

    public void setExecutorId(String executorId) {
        this.executorId = executorId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
