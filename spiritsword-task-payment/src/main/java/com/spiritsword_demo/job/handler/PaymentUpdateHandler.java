package com.spiritsword_demo.job.handler;

import com.spiritsword.config.SpiritswordTaskHandler;
import com.spiritsword.task.handler.TaskHandler;
import com.spiritsword.task.model.TaskResult;
import com.spiritsword.task.model.TaskResultEnum;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@SpiritswordTaskHandler(executorId = "executor-payment")
public class PaymentUpdateHandler implements TaskHandler {
    @Override
    public TaskResult handle(Map<String, Object> params) {
        System.out.println("PaymentUpdateHandler handle");
        return TaskResult.buildResult(params, TaskResultEnum.SUCCESS, this.getClass().getName(), "Payment Information");
    }

    @Override
    public String handlerTaskDescription() {
        return "Payment Information Update";
    }

    @Override
    public String handlerTaskName() {
        return "Handle payment information";
    }
}
