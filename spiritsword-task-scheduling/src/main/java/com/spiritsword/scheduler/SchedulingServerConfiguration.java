package com.spiritsword.scheduler;

import com.spiritsword.scheduler.repository.Repository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@ComponentScan
public class SchedulingServerConfiguration {

    @Value("${spiritsword.thread.core}")
    private int corePoolSize;

    @Value("${spiritsword.thread.max}")
    private int maxPoolSize;

    @Bean
    public Executor threadExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Bean
    @DependsOn("executorRegistry")
    public TaskScheduler taskScheduler(TaskDistributor taskDistributor) {
        TaskScheduler taskScheduler = new TaskScheduler(taskDistributor);
        taskScheduler.start();
        return taskScheduler;
    }

    @Bean
    public TaskDistributor taskDistributor(Repository repository, ExecutorManager executorManager, TaskErrorProcessor taskErrorProcessor, Executor threadExecutor) {
        return new DelayedQueueTaskDistributor(repository, threadExecutor, executorManager, taskErrorProcessor);
    }

    @Bean
    public DefaultTaskErrorProcessor defaultTaskErrorProcessor(Repository repository) {
        DefaultTaskErrorProcessor defaultTaskErrorProcessor = new DefaultTaskErrorProcessor();
        defaultTaskErrorProcessor.setRepository(repository);
        return defaultTaskErrorProcessor;
    }

    @Bean
    public ExecutorManager executorManager() {
        return new StandaloneExecutorManager();
    }
}
