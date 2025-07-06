package com.spiritsword.repository;

import com.spiritsword.task.model.Task;
import com.spiritsword.task.model.TaskStateEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "spiritsword.datasource")
public class TaskRepository {
    private String url;
    private String driver;
    private String username;
    private String password;

    Connection connection;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void connect() throws ClassNotFoundException {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateTask(TaskStateEnum taskState, LocalDateTime lastTriggerTime, LocalDateTime nextTriggerTime) {

    }

    @SuppressWarnings("all")
    public List<Task> findTasksAboutDue() {
        String sql = "SELECT * FROM TASK WHERE task_state = ? AND next_trigger_time > ? AND next_trigger_time < ?";
        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, "READY");
            pst.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pst.setTimestamp(3, new Timestamp(System.currentTimeMillis() + 2 * 60 * 1000));

            ResultSet rs = pst.executeQuery();

            List<Task> tasks = new ArrayList<>();

            while(rs.next()){
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
                task.setRetryInterval(rs.getInt("retry_interval"));
                task.setExecutor(rs.getString("executor"));
                task.setHandlerId(rs.getString("handler_id"));
                task.setCreated(rs.getTimestamp("created").toLocalDateTime());
                task.setUpdated(rs.getTimestamp("updated").toLocalDateTime());
                task.setVersion(rs.getInt("version"));

                tasks.add(task);
            }

            return tasks;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
