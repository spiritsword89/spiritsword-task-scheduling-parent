package com.spiritsword.task.model;

import java.util.Map;

public class TaskRequest {
    // Key: Parameter Type, Value: Parameter Value
    private Map<String, Object> params;

    //Handler Id
    private String handlerId;

    private String handlerClass;

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(String handlerId) {
        this.handlerId = handlerId;
    }

    public String getHandlerClass() {
        return handlerClass;
    }

    public void setHandlerClass(String handlerClass) {
        this.handlerClass = handlerClass;
    }
}
