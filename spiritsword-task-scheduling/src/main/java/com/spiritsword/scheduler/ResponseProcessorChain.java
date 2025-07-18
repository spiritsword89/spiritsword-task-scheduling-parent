package com.spiritsword.scheduler;

import com.spiritsword.task.model.TaskResult;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResponseProcessorChain implements SmartInitializingSingleton {

    @Autowired
    private List<ResponseProcessor> responseProcessors;

    private int currentIndex = 0;

    public void doProcess(TaskResult taskResult) {
        if(currentIndex < responseProcessors.size()) {
            ResponseProcessor responseProcessor = responseProcessors.get(currentIndex++);
            responseProcessor.process(taskResult, this);
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        responseProcessors.sort((p1, p2) -> {
            if(p1 instanceof DefaultResponseProcessor) {
                return -1;
            }

            if(p2 instanceof DefaultResponseProcessor) {
                return 1;
            }

            return 0;
        });
    }
}
