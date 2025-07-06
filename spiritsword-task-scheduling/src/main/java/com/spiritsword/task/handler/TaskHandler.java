package com.spiritsword.task.handler;


import com.spiritsword.task.model.TaskResult;

public interface TaskHandler {
    public TaskResult handle(String params);
}
