package com.spiritsword.task.model;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayedTask implements Delayed {

    private Task task;
    private long executeAt;

    public DelayedTask(Task task, long delayMillis) {
        this.task = task;
        this.executeAt = System.currentTimeMillis() + delayMillis;
    }

    public DelayedTask() {}

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(executeAt - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return Long.compare(this.executeAt, ((DelayedTask) o).executeAt);
    }

    public Task getTask() {
        return this.task;
    }
}
