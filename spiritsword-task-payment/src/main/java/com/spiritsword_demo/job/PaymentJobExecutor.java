package com.spiritsword_demo.job;

import com.spiritsword.config.SpiritswordExecutor;
import com.spiritsword.task.excutor.BaseTaskExecutor;
import com.spiritsword.task.model.ChannelMessage;
import com.spiritsword.task.model.TaskResult;
import com.spiritsword_demo.PaymentApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpiritswordExecutor(executorId = "executor-payment", executorType = "payment")
public class PaymentJobExecutor extends BaseTaskExecutor {
    private static final Logger logger = LoggerFactory.getLogger(PaymentApplication.class);
    @Override
    protected void beforeExecute(ChannelMessage channelMessage) {
        logger.info("before payment job executor execute: ");
    }

    @Override
    protected void afterExecute(TaskResult result) {
        logger.info("after payment job executor execute: {}", result.getMessage());
    }
}
