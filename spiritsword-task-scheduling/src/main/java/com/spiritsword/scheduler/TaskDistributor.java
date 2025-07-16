package com.spiritsword.scheduler;

import com.spiritsword.task.model.Task;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface TaskDistributor {
    public void startProcessTasks();
    public void distributeTasks(Task task) throws Exception;
    public void addTasks(List<Task> tasks);
    public void applyExecutorManager(ExecutorManager executorManager);
    public ExecutorManager getExecutorManager();
    public void pullingTasksFromRepository(int initialDelay, int interval, TimeUnit timeUnit);
}
