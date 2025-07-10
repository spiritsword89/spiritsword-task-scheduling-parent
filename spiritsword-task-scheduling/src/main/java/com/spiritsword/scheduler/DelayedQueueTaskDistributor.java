package com.spiritsword.scheduler;

import com.spiritsword.exceptions.ExecutorNotFoundException;
import com.spiritsword.repository.Repository;
import com.spiritsword.task.model.*;
import com.spiritsword.utils.CronUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
public class DelayedQueueTaskDistributor implements TaskDistributor {
    private static Logger logger = LoggerFactory.getLogger(DelayedQueueTaskDistributor.class);

    private ExecutorManager executorManager;

    private ScheduledFuture<?> scheduledPullTaskFuture;

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    private LinkedBlockingQueue<Task> cacheTasks = new LinkedBlockingQueue<>();

    private DelayQueue<DelayedTask> delayedTaskQueue = new DelayQueue<>();

    @Autowired
    private Repository repository;

    @Autowired
    private Executor threadExecutor;

    @Autowired
    private TaskErrorProcessor taskErrorProcessor;

    @Override
    public void processTasks() {
        new Thread(() -> {
            while(true) {
                try {
                    Task task = cacheTasks.take();
                    if(task.getTaskState().equals(TaskStateEnum.RUNNING)) {
                        continue;
                    }
                    // 2. 如果任务的nextTriggerTime已经小于目前时间，那证明任务要立即开始
                    if (task.getNextTriggerTime() != null && task.getNextTriggerTime().getSecond() * 1000 < System.currentTimeMillis()) {
                        threadExecutor.execute(() -> {
                            try {
                                distributeTasks(task);
                            } catch (Exception e) {
                                logger.error(e.getMessage(), e);
                                //处理异常
                                //扩展点： 提供默认错误处理器接口，用户可以自己定义逻辑
                                taskErrorProcessor.process(task);
                            }
                        });
                    } else {
                        // 3. 否则将还没到期的任务放进DelayedQueue，到期弹出执行
                        long delayMillis = task.getNextTriggerTime().getSecond() * 1000 - System.currentTimeMillis();
                        repository.updateTask(TaskStateEnum.WAITING, task.getId());
                        DelayedTask delayedTask = new DelayedTask(task, delayMillis);
                        delayedTaskQueue.offer(delayedTask);
                    }
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Override
    public void distributeTasks(Task task) throws Exception {
        if(validateTask(task)) {
            //无效Task，需要删除 todo
            return;
        }
        // 1. 挑选出可用并且对应的Executor， 找到对应的Executor Channel
        ExecutorInfo selectedExecutorInfo;

        if(task.getExecutor() != null) {
            selectedExecutorInfo = executorManager.getExecutorInfo(task.getExecutor());
        } else {
            selectedExecutorInfo = executorManager.selectExecutorInfoByType(task.getExecutorType(), task.getHandlerClass());
        }

        if(selectedExecutorInfo == null) {
            logger.error("Executor Not Found");
            throw new ExecutorNotFoundException(task.getExecutor(), task.getExecutorType(), task.getHandlerClass());
        }

        // 2. 同步状态进数据库： 执行状态 -> RUNNING, 更新lastTriggeredTime和nextTriggerTime
        //计算nextTriggerTime;
        LocalDateTime nextTriggerTime = null;
        if(task.getCronExpression() != null) {
            nextTriggerTime = CronUtils.getNextTriggerTime(task.getCronExpression());
        }

        repository.updateTask(TaskStateEnum.RUNNING, LocalDateTime.now(), nextTriggerTime, task.getId());

        ChannelMessage.ChannelMessageBuilder builder = new ChannelMessage.ChannelMessageBuilder();
        ChannelMessage channelMessage = builder
                .messageType(MessageType.TASK_REQUEST)
                .executorId(selectedExecutorInfo.getExecutorId())
                .taskId(task.getId().toString())
                .handlerId(task.getHandlerId())
                .handlerClass(task.getHandlerClass())
                .params(task.getPayload())
                .build();

        executorManager.dispatch(selectedExecutorInfo.getExecutorId(), channelMessage);
    }

    private boolean validateTask(Task task) {
        if(task.getExecutor() == null && task.getHandlerClass() == null && task.getExecutorType() == null) {
            return false;
        }

        if(task.getTaskState().equals(TaskStateEnum.RUNNING)) {
            return false;
        }

        return true;
    }

    @Override
    public void addTasks(List<Task> tasks) {
        this.cacheTasks.addAll(tasks);
    }

    @Override
    @Autowired
    public void applyExecutorManager(ExecutorManager executorManager) {
        if(this.executorManager == null){
            throw new RuntimeException("Executor Manager is not found");
        }
        this.executorManager = executorManager;

        new Thread(() -> {
            while(true) {
                try {
                    DelayedTask delayedTask = delayedTaskQueue.take();
                    threadExecutor.execute(() -> {
                        try {
                            distributeTasks(delayedTask.getTask());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Override
    public ExecutorManager getExecutorManager() {
        return this.executorManager;
    }

    @Override
    public void pullingTasksFromRepository(int initialDelay, int interval, TimeUnit timeUnit) {
        if(this.scheduledPullTaskFuture != null) {
            this.scheduledPullTaskFuture.cancel(false);
        }

        this.scheduledPullTaskFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            List<Task> tasksAboutDue = repository.findTasksAboutDue(cacheTasks.stream().map(Task::getId).collect(Collectors.toList()));
            addTasks(tasksAboutDue);
        }, initialDelay, interval, timeUnit);
    }
}
