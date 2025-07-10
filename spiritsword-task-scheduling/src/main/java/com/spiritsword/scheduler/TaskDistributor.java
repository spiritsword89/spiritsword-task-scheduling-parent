package com.spiritsword.scheduler;

import com.spiritsword.task.model.Task;

import java.util.List;

public interface TaskDistributor {
    public void processTasks();
    public void distributeTasks(Task task);
    public void addTasks(List<Task> tasks);
    public void applyExecutorManager(ExecutorManager executorManager);
}
