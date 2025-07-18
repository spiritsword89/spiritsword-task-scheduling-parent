package com.spiritsword.task.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class TaskResult implements Serializable {
    private String taskId;
    private String handlerClass;
    private String message;
    private TaskResultEnum state;
    private long duration;
    private String stackTrace;
    private Date finishTime;
    private boolean retryable;

    public static TaskResult buildResult(Map<String, Object> params, TaskResultEnum state, String handlerClass, String message) {
        TaskResult taskResult = new TaskResult();
        taskResult.setState(state);
        taskResult.setFinishTime(new Date());
        taskResult.setHandlerClass(handlerClass);
        taskResult.setMessage(message);
        taskResult.setTaskId(params.get("taskId").toString());

        return taskResult;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getHandlerClass() {
        return handlerClass;
    }

    public void setHandlerClass(String handlerClass) {
        this.handlerClass = handlerClass;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TaskResultEnum getState() {
        return state;
    }

    public void setState(TaskResultEnum state) {
        this.state = state;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public boolean isRetryable() {
        return retryable;
    }

    public void setRetryable(boolean retryable) {
        this.retryable = retryable;
    }
}
