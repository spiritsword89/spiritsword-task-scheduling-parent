package com.spiritsword.repository;

import com.spiritsword.task.model.Task;
import com.spiritsword.task.model.TaskStateEnum;

import java.time.LocalDateTime;
import java.util.List;

public interface Repository {
    public void connect();
    public int insertTask(Task task);
    public int updateTask(TaskStateEnum taskState, LocalDateTime lastTriggerTime, LocalDateTime nextTriggerTime, int taskId);
    public List<Task> findTasksAboutDue();
}
