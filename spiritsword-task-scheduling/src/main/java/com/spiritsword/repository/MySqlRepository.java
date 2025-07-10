package com.spiritsword.repository;

import com.spiritsword.task.model.Task;
import com.spiritsword.task.model.TaskStateEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ConfigurationProperties(prefix = "spiritsword.datasource")
public class MySqlRepository extends BaseRepository{
    private static final Logger logger = LoggerFactory.getLogger(MySqlRepository.class);

    @Override
    public int insertTask(Task task) {
        String sql = "INSERT INTO TASK VALUES (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, task.getTaskName());
            pst.setString(2, task.getTaskDescription());
            pst.setString(3, task.getCronExpression());
            pst.setString(4, task.getTaskState().name().toUpperCase());
            pst.setString(5, task.getPayload());
            pst.setTimestamp(6, Timestamp.valueOf(task.getLastTriggerTime()));
            pst.setTimestamp(7, Timestamp.valueOf(task.getNextTriggerTime()));
            pst.setInt(8, task.getRetryCount());
            pst.setInt(9, task.getMaxRetryCount());
            pst.setLong(10, task.getRetryInterval());
            pst.setString(11, task.getExecutor());
            pst.setString(12, task.getHandlerId());
            pst.setTimestamp(13, Timestamp.valueOf(task.getCreated()));
            pst.setTimestamp(14, Timestamp.valueOf(task.getUpdated()));
            pst.setInt(15, task.getVersion());
            pst.setString(16, task.getHandlerClass());
            pst.setString(17, task.getExecutorType());

            return pst.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public int updateTask(TaskStateEnum taskStateEnum, Integer taskId) {
        String sql = "UPDATE TASK SET task_state = ? WHERE id = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, taskStateEnum.name().toUpperCase());
            pst.setInt(2, taskId);
            return pst.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public int updateTask(TaskStateEnum taskState, LocalDateTime lastTriggerTime, LocalDateTime nextTriggerTime, int taskId) {
        String sql = "UPDATE TASK SET task_state = ?, last_trigger_time = ?, next_trigger_time = ? WHERE id = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, taskState.name().toUpperCase());
            pst.setTimestamp(2, Timestamp.valueOf(lastTriggerTime));
            pst.setTimestamp(3, Timestamp.valueOf(nextTriggerTime));
            pst.setInt(4, taskId);
            return pst.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("all")
    @Override
    public List<Task> findTasksAboutDue(List<Integer> excludeTasks) {

        String placeholders = excludeTasks.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));

        String sql = "SELECT * FROM TASK WHERE task_state IN ('READY', 'RETRY') AND next_trigger_time > ? AND next_trigger_time < ? AND id NOT IN ("+ placeholders +")";

        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pst.setTimestamp(2, new Timestamp(System.currentTimeMillis() + 2 * 60 * 1000));

            for (int i = 0; i < excludeTasks.size(); i++) {
                pst.setInt(i + 3, excludeTasks.get(i));
            }

            ResultSet rs = pst.executeQuery();

            List<Task> tasks = new ArrayList<>();

            while(rs.next()){
                Task task = populateFields(rs);
                tasks.add(task);
            }

            return tasks;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Task populateFields(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setTaskName(rs.getString("task_name"));
        task.setTaskDescription(rs.getString("task_description"));
        task.setNextTriggerTime(rs.getTimestamp("next_trigger_time").toLocalDateTime());
        task.setLastTriggerTime(rs.getTimestamp("last_trigger_time").toLocalDateTime());
        task.setCronExpression(rs.getString("cron_expression"));
        task.setTaskState(TaskStateEnum.valueOf(rs.getString("task_state")));
        task.setPayload(rs.getString("payload"));
        task.setRetryCount(rs.getInt("retry_count"));
        task.setMaxRetryCount(rs.getInt("max_retry_count"));
        task.setRetryInterval(rs.getLong("retry_interval"));
        task.setExecutor(rs.getString("executor"));
        task.setHandlerId(rs.getString("handler_id"));
        task.setCreated(rs.getTimestamp("created").toLocalDateTime());
        task.setUpdated(rs.getTimestamp("updated").toLocalDateTime());
        task.setVersion(rs.getInt("version"));
        task.setHandlerClass(rs.getString("handler_class"));
        task.setExecutorType(rs.getString("executor_type"));
        return task;
    }

    @Override
    public Task findTaskById(Integer taskId) {
        String sql = "SELECT * FROM TASK WHERE id = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, taskId);
            ResultSet rs = pst.executeQuery();
            return populateFields(rs);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public int updateRetryTask(LocalDateTime lastTriggerTime, int taskId) {
        Task task = findTaskById(taskId);
        if(task.getRetryCount() < task.getMaxRetryCount()){
            long nextTrigger = task.getRetryInterval() + System.currentTimeMillis();
            Timestamp nextTriggerTime = new Timestamp(nextTrigger);
            int retryCount = task.getRetryCount();
            String sql = "UPDATE TASK SET task_state = ?, retry_count = ?, next_trigger_time = ? WHERE id = ?";
            try {
                PreparedStatement pst = connection.prepareStatement(sql);
                pst.setString(1, TaskStateEnum.RETRY.name().toUpperCase());
                pst.setLong(2, retryCount + 1);
                pst.setTimestamp(3, nextTriggerTime);
                pst.setInt(4, taskId);

                return pst.executeUpdate();
            } catch (SQLException e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
        } else {
            String sql = "UPDATE TASK SET task_state = ?, last_trigger_time = ? WHERE id = ?";
            try {
                PreparedStatement pst = connection.prepareStatement(sql);
                pst.setString(1, TaskStateEnum.FAILED.name().toUpperCase());
                pst.setTimestamp(2, Timestamp.valueOf(lastTriggerTime));
                pst.setInt(3, taskId);

                return pst.executeUpdate();
            } catch (SQLException e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
