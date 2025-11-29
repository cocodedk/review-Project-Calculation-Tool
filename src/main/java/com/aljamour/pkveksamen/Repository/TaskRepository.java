package com.aljamour.pkveksamen.Repository;

import com.aljamour.pkveksamen.Model.Task;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TaskRepository {
    private final JdbcTemplate jdbcTemplate;

    public TaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTask(Task task) {
        String sql = "INSERT INTO task (employee_id, sub_project_id, task_title, task_description, task_status, " +
                "task_start_date, task_end_date, task_duration, task_priority, task_note) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                null, // employee_id - map from task model if exists
                null, // sub_project_id - map from task model if exists
                task.getTaskName(),
                task.getTaskDescription(),
                task.getTaskStatus(),
                task.getStartDate(),
                task.getEndDate(),
                task.getDuration() != null ? Integer.parseInt(task.getDuration()) : null,
                null, // task_priority - not in current Task model
                task.getTaskNote()
        );
    }

    public List<Task> findBySubProjectId(int subProjectId) {
        String sql = "SELECT task_id, employee_id, sub_project_id, task_title, task_description, task_status, " +
                "task_start_date, task_end_date, task_duration, task_priority, task_note " +
                "FROM task WHERE sub_project_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Task task = new Task();
            task.setTaskID(rs.getInt("task_id"));
            task.setTaskName(rs.getString("task_title"));
            task.setTaskDescription(rs.getString("task_description"));
            task.setTaskStatus(rs.getString("task_status"));
            task.setTaskNote(rs.getString("task_note"));
            task.setStartDate(rs.getObject("task_start_date", java.time.LocalDate.class));
            task.setEndDate(rs.getObject("task_end_date", java.time.LocalDate.class));
            task.setDuration(rs.getObject("task_duration", Integer.class) != null ? rs.getObject("task_duration", Integer.class).toString() : null);
            return task;
        }, subProjectId);
    }

    public Task findById(int taskId) {
        String sql = "SELECT task_id, employee_id, sub_project_id, task_title, task_description, task_status, " +
                "task_start_date, task_end_date, task_duration, task_priority, task_note " +
                "FROM task WHERE task_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Task task = new Task();
            task.setTaskID(rs.getInt("task_id"));
            task.setTaskName(rs.getString("task_title"));
            task.setTaskDescription(rs.getString("task_description"));
            task.setTaskStatus(rs.getString("task_status"));
            task.setTaskNote(rs.getString("task_note"));
            task.setStartDate(rs.getObject("task_start_date", java.time.LocalDate.class));
            task.setEndDate(rs.getObject("task_end_date", java.time.LocalDate.class));
            task.setDuration(rs.getObject("task_duration", Integer.class) != null ? rs.getObject("task_duration", Integer.class).toString() : null);
            return task;
        }, taskId);
    }
}