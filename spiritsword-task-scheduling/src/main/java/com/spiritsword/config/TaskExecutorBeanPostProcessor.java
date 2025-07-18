package com.spiritsword.config;

import com.spiritsword.task.excutor.BaseTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.NoSuchMessageException;
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

            Class<?> targetBeanClass = bean.getClass();

            if(executorId.isEmpty()) {
                executorId = "executor-" + targetBeanClass.getSimpleName();
            }

            String executorType = annotation.executorType() != null ? annotation.executorType() : null;

            Field executorIdField = null;
            Field executorTypeField = null;

            while (executorIdField == null) {
                try {
                    executorIdField = targetBeanClass.getDeclaredField(BaseTaskExecutor.EXECUTOR_ID_FIELD);
                    executorTypeField = targetBeanClass.getDeclaredField(BaseTaskExecutor.EXECUTOR_TYPE_FIELD);
                }catch (NoSuchFieldException e) {
                    targetBeanClass = targetBeanClass.getSuperclass();
                }
            }

            if(executorIdField == null || executorTypeField == null) {
                logger.error("Methods {}, {} not found", BaseTaskExecutor.EXECUTOR_ID_FIELD, BaseTaskExecutor.EXECUTOR_TYPE_FIELD);
                throw new NoSuchMessageException("No such method");
            }

            try {
                executorIdField.setAccessible(true);
                executorIdField.set(bean, executorId);

                executorTypeField.setAccessible(true);
                executorTypeField.set(bean, executorType);
            }catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        return bean;
    }
}
