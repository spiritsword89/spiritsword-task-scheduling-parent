package com.spiritsword.config;

import com.spiritsword.task.excutor.BaseTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class TaskExecutorBeanPostProcessor implements BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TaskExecutorBeanPostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().isAnnotationPresent(SpiritswordExecutor.class)) {
            SpiritswordExecutor annotation = bean.getClass().getAnnotation(SpiritswordExecutor.class);
            String executorId = annotation.executorId();

            if(executorId.isEmpty()) {
                executorId = "executor-" + bean.getClass().getSimpleName();
            }

            String executorType = annotation.executorType() != null ? annotation.executorType() : null;

            try {
                Field executorIdField = bean.getClass().getDeclaredField(BaseTaskExecutor.EXECUTOR_ID_FIELD);
                executorIdField.set(bean, executorId);

                Field executorTypeField = bean.getClass().getDeclaredField(BaseTaskExecutor.EXECUTOR_TYPE_FIELD);
                executorTypeField.set(bean, executorType);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

        return bean;
    }
}
