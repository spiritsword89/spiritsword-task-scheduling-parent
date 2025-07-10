package com.spiritsword.task.model;

import com.alibaba.fastjson2.annotation.JSONField;
import com.spiritsword.task.model.serialize.MessageTypeDeserializer;
import com.spiritsword.task.model.serialize.MessageTypeSerializer;

public class ChannelMessage {
    @JSONField(serializeUsing = MessageTypeSerializer.class,  deserializeUsing = MessageTypeDeserializer.class)
    private MessageType messageType;
    private String executorId;
    private String taskId;
    private Object payload;

    public ChannelMessage() {

    }

    public ChannelMessage(MessageType messageType) {
        this.messageType = messageType;
    }

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
