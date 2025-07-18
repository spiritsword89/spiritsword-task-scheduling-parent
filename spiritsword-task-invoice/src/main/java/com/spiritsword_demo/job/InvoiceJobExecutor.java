package com.spiritsword_demo.job;

import com.spiritsword.config.SpiritswordExecutor;
import com.spiritsword.task.excutor.BaseTaskExecutor;
import com.spiritsword.task.model.ChannelMessage;
import com.spiritsword.task.model.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpiritswordExecutor(executorId = "executor-invoice", executorType = "invoice")
public class InvoiceJobExecutor extends BaseTaskExecutor {
    private static final Logger log = LoggerFactory.getLogger(InvoiceJobExecutor.class);
    @Override
    protected void beforeExecute(ChannelMessage channelMessage) {
        log.info("Before Job executes: invoice");
    }

    @Override
    protected void afterExecute(TaskResult result) {
        log.info("After Job executes, message: {}", result.getMessage());
    }
}
