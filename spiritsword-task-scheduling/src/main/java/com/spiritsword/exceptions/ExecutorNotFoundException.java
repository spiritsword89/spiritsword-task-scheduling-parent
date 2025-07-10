package com.spiritsword.exceptions;

public class ExecutorNotFoundException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "The task calling for executor with id = %s, type = %s, handler = %s is not found";

    public ExecutorNotFoundException(String executorId, String executorType, String handlerClassName) {
        super(String.format(MESSAGE_TEMPLATE, executorId, executorType, handlerClassName));
    }
}
