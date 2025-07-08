package com.spiritsword.task.model;

public enum MessageType {
    SCHEDULER_REGISTER(1),
    EXECUTOR_REGISTER(2),
    TASK_REQUEST(3),
    TASK_RESPONSE(4),
    EXECUTOR_STATE(5),
    PULL_REQUEST(6),
    EXECUTOR_LIST(7);

    final int type;

    MessageType(int type){
        this.type = type;
    }

    public int getType(){
        return this.type;
    }
}
