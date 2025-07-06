package com.spiritsword.task.excutor;

public interface TaskExecutor {
    public String getExecutorId();
    public void execute();
    public boolean supports(String handlerClass);
    public double getLoad();
    public boolean isHealthy();
}
