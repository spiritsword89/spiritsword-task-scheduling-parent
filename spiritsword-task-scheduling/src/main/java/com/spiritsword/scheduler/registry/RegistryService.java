package com.spiritsword.scheduler.registry;

import com.spiritsword.task.model.ExecutorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
public class RegistryService {
    private static final Logger log = LoggerFactory.getLogger(RegistryService.class);
    // 维护：taskType → 执行器列表
    private final Map<String, List<ExecutorInfo>> registryMap = new ConcurrentHashMap<>();

    public RegistryService() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        scheduledExecutorService.schedule(this::removeDeadExecutors, 30, TimeUnit.SECONDS);
    }

    public void updateExecutorInfo(String taskType, ExecutorInfo executor) {
        List<ExecutorInfo> executorInfos = registryMap.get(taskType);
        executorInfos.stream().filter(item -> item.getExecutorId().equals(executor.getExecutorId())).findFirst().ifPresent(item -> {
            item.setLastHeartbeat(System.currentTimeMillis());
        });
    }

    public void register(String taskType, ExecutorInfo executor) {
        registryMap.computeIfAbsent(taskType, k -> new CopyOnWriteArrayList<>()).add(executor);
        log.info("Map size: {}", registryMap.size());
    }

    public List<ExecutorInfo> getExecutors(String executorTaskType) {
        return registryMap.getOrDefault(executorTaskType, Collections.emptyList());
    }

    public List<ExecutorInfo> getAllAvailableExecutors() {
        return registryMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public void removeDeadExecutors() {

    }
}
