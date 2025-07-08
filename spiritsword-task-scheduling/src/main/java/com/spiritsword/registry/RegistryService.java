package com.spiritsword.registry;

import com.spiritsword.task.model.ExecutorInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class RegistryService {
    // 维护：taskType → 执行器列表
    private final Map<String, List<ExecutorInfo>> registryMap = new ConcurrentHashMap<>();

    public RegistryService() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        scheduledExecutorService.schedule(this::removeDeadExecutors, 30, TimeUnit.SECONDS);
    }

    public void register(String taskType, ExecutorInfo executor) {
        registryMap.computeIfAbsent(taskType, k -> new CopyOnWriteArrayList<>()).add(executor);
    }

    public List<ExecutorInfo> getExecutors(String taskType) {
        return registryMap.getOrDefault(taskType, Collections.emptyList());
    }

    public List<ExecutorInfo> getAllAvailableExecutors() {
        return registryMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public void removeDeadExecutors() {

    }
}
