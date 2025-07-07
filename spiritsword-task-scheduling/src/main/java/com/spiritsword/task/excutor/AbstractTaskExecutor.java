package com.spiritsword.task.excutor;

import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractTaskExecutor implements TaskExecutor, InitializingBean {

    private String executorId;

    @Override
    public String getExecutorId() {
        return executorId;
    }

    public void setExecutorId(String executorId) {
        this.executorId = executorId;
    }

    private void connect() {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(this::connect).start();
    }
}
