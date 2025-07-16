package com.spiritsword.scheduler;

import com.spiritsword.task.model.TaskResult;

public interface ResponseProcessor {
    public void process(TaskResult taskResult);
}
