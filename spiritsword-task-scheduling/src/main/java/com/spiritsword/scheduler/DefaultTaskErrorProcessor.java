package com.spiritsword.scheduler;

import com.spiritsword.repository.Repository;
import com.spiritsword.task.model.Task;

public class DefaultTaskErrorProcessor implements TaskErrorProcessor {

    private Repository repository;

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void process(Task task) {

    }
}
