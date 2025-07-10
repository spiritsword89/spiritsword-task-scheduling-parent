package com.spiritsword.scheduler;

import com.spiritsword.task.model.Task;

public interface TaskErrorProcessor {
    public void process(Task task);
}
