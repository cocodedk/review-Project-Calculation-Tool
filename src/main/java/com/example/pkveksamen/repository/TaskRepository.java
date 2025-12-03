package com.example.pkveksamen.repository;

import com.example.pkveksamen.model.Task;
import com.example.pkveksamen.model.Priority;
import com.example.pkveksamen.model.Status;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class TaskRepository {
    private final JdbcTemplate jdbcTemplate;

    public TaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTask(Integer employeeId, long subProjectId, String taskName, String taskDescription
    , Status status, LocalDate startDate, LocalDate endDate, int taskDuration, Priority priority, String taskNote) {

        jdbcTemplate.update(
                "INSERT INTO task (employee_id, sub_project_id, task_title, task_description, task_status, " +
                        "task_start_date, task_end_date, task_duration, task_priority, task_note) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",

                employeeId,
                subProjectId,
                taskName,
                taskDescription,
                status,
                startDate,
                endDate,
                taskDuration,
                priority,
                taskNote

        );
    }

    public List<Task> showTaskByEmployeeId(int employeeId) {
        String sql = "SELECT task_id, employee_id, sub_project_id, task_title, task_description, task_status, " +
                "task_start_date, task_end_date, task_duration, task_priority, task_note " +
                "FROM task WHERE sub_project_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Task task = new Task();
            task.setTaskID(rs.getInt("task_id"));
            task.setTaskName(rs.getString("task_title"));
            task.setTaskDescription(rs.getString("task_description"));
            task.setTaskStatus(Status.valueOf(rs.getString("task_status")));
            task.setTaskNote(rs.getString("task_note"));
            task.setStartDate(rs.getObject("task_start_date", java.time.LocalDate.class));
            task.setEndDate(rs.getObject("task_end_date", java.time.LocalDate.class));
            task.setTaskDuration(rs.getInt("task_duration"));
            task.recalculateDuration();
            return task;
        }, employeeId);
    }

    public void saveTask(Task task, long subProjectID) {
        // Beregn varighed ud fra datoerne
        task.recalculateDuration();

        String sql = "INSERT INTO Task " +
                "(task_id, employye_id,sub_project_id, task_title, task_description, task_status, task_start_date," +
                "task_end_date,task_duration,task_priority,task_note) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        jdbcTemplate.update(sql,
                subProjectID,
                task.getTaskName(),
                task.getTaskDescription(),
                task.getTaskStatus(),
                task.getStartDate(),
                task.getEndDate(),
                task.getTaskDuration(),
                task.getTaskPriority(),
                task.getTaskNote()

        );
    }

}