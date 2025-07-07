package com.spiritsword.scheduler;

import com.spiritsword.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
public class TaskScheduler {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private Executor executor;

    public void start() {
        connect();
        schedule();
    }

    private void connect() {

    }

    private void schedule() {

    }
}
