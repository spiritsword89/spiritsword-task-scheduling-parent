package com.spiritsword.task.handler;


import com.spiritsword.task.model.TaskResult;

import java.util.Map;

public interface TaskHandler {
    public TaskResult handle(Map<String, Object> params);
    public String getHandlerId();
}
