package com.spiritsword.scheduler;

import com.spiritsword.scheduler.repository.Repository;
import com.spiritsword.task.model.TaskResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultResponseProcessor implements  ResponseProcessor {

    @Autowired
    private Repository repository;

    public DefaultResponseProcessor() {
    }

    @Override
    public void process(TaskResult taskResult) {
        repository.updateTaskResult(taskResult);
    }
}
