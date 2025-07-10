package com.spiritsword.task.model;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import com.spiritsword.task.model.serialize.MessageTypeDeserializer;
import com.spiritsword.task.model.serialize.MessageTypeSerializer;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ChannelMessage {
    @JSONField(serializeUsing = MessageTypeSerializer.class,  deserializeUsing = MessageTypeDeserializer.class)
    private MessageType messageType;
    private String executorId;
    private String taskId;
    private Object payload;

    public ChannelMessage() {

    }

    public ChannelMessage(ChannelMessageBuilder builder) {
        this.messageType = builder.messageType;
        this.executorId = builder.executorId;
        this.taskId = builder.taskId;

        if(builder.messageType.equals(MessageType.TASK_REQUEST)) {
            TaskRequest taskRequest = new TaskRequest();
            taskRequest.setHandlerId(builder.handlerId);
            taskRequest.setHandlerClass(builder.handlerClass);

            if(builder.params != null) {
                Map<String, Object> params = JSON.parseObject(builder.params, Map.class);
                taskRequest.setParams(params);
            }
            this.payload = taskRequest;
        } else if (builder.messageType.equals(MessageType.TASK_RESPONSE)) {
            TaskResult taskResult = new TaskResult();
            taskResult.setTaskId(builder.taskId);
            taskResult.setSuccess(builder.success);
            taskResult.setMessage(builder.message);
            taskResult.setHandlerClass(builder.handlerClass);
            taskResult.setState(builder.state);
            taskResult.setDuration(builder.duration);
            taskResult.setStackTrace(builder.stackTrace);
            taskResult.setFinishTime(builder.finishTime);
            taskResult.setRetryable(builder.retryable);
            this.payload = taskResult;
        } else if (builder.messageType.equals(MessageType.EXECUTOR_STATE)) {
            ExecutorInfo executorInfo = new ExecutorInfo();
            executorInfo.setExecutorId(builder.executorId);
            executorInfo.setExecutorTaskType(builder.executorTaskType);
            executorInfo.setHandlerClassList(builder.handlerClassList);
            executorInfo.setHost(builder.host);
            executorInfo.setPort(builder.port);
            executorInfo.setLastHeartbeat(builder.lastHeartbeat);
            this.payload = executorInfo;
        }

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

    public static class ChannelMessageBuilder {
        private MessageType messageType;
        private String executorId;
        private String taskId;
        private String params;
        private String handlerId;
        private String handlerClass;

        private boolean success;
        private String message;
        private TaskResultEnum state;
        private long duration;
        private String stackTrace;
        private Date finishTime;
        private boolean retryable;

        private String executorTaskType;
        private List<String> handlerClassList;
        private String host;
        private int port;
        private long lastHeartbeat;

        public ChannelMessageBuilder() {

        }

        public ChannelMessage build() {
            return new ChannelMessage(this);
        }

        public ChannelMessageBuilder messageType(MessageType messageType) {
            this.messageType = messageType;
            return this;
        }

        public ChannelMessageBuilder executorId(String executorId) {
            this.executorId = executorId;
            return this;
        }

        public ChannelMessageBuilder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public ChannelMessageBuilder params(String params) {
            this.params = params;
            return this;
        }

        public ChannelMessageBuilder handlerId(String handlerId) {
            this.handlerId = handlerId;
            return this;
        }

        public ChannelMessageBuilder handlerClass(String handlerClass) {
            this.handlerClass = handlerClass;
            return this;
        }

        public ChannelMessageBuilder success(boolean success) {
            this.success = success;
            return this;
        }

        public ChannelMessageBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ChannelMessageBuilder state(TaskResultEnum state) {
            this.state = state;
            return this;
        }

        public ChannelMessageBuilder duration(long duration) {
            this.duration = duration;
            return this;
        }

        public ChannelMessageBuilder stackTrace(String stackTrace) {
            this.stackTrace = stackTrace;
            return this;
        }

        public ChannelMessageBuilder finishTime(Date finishTime) {
            this.finishTime = finishTime;
            return this;
        }

        public ChannelMessageBuilder retryable(boolean retryable) {
            this.retryable = retryable;
            return this;
        }

        public ChannelMessageBuilder executorTaskType(String executorTaskType) {
            this.executorTaskType = executorTaskType;
            return this;
        }

        public ChannelMessageBuilder handlerClassList(List<String> handlerClassList) {
            this.handlerClassList = handlerClassList;
            return this;
        }

        public ChannelMessageBuilder host(String host) {
            this.host = host;
            return this;
        }

        public ChannelMessageBuilder port(int port) {
            this.port = port;
            return this;
        }

        public ChannelMessageBuilder lastHeartbeat(long lastHeartbeat) {
            this.lastHeartbeat = lastHeartbeat;
            return this;
        }
    }
}
