package com.spiritsword;

import com.spiritsword.repository.Repository;
import com.spiritsword.scheduler.DefaultTaskErrorProcessor;
import com.spiritsword.scheduler.TaskErrorProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class TaskSchedulingConfiguration {

    @Bean
    @ConditionalOnMissingBean(TaskErrorProcessor.class)
    public DefaultTaskErrorProcessor defaultTaskErrorProcessor(Repository repository) {
        DefaultTaskErrorProcessor defaultTaskErrorProcessor = new DefaultTaskErrorProcessor();
        defaultTaskErrorProcessor.setRepository(repository);
        return defaultTaskErrorProcessor;
    }
}
