
package com.example.pkveksamen.repository;

import com.example.pkveksamen.model.SubTask;
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
                status.name(),
                startDate,
                endDate,
                taskDuration,
                priority.name(),
                taskNote

        );
    }

    public List<Task> showTaskByEmployeeId(int employeeId) {
        String sql = "SELECT task_id, employee_id, sub_project_id, task_title, task_description, task_status, " +
                "task_start_date, task_end_date, task_duration, task_priority, task_note " +
                "FROM task WHERE employee_id = ?";
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


    public void saveTask(Task task, int employeeId, long projectId, long subProjectId) {
        String sql = "INSERT INTO task (user_id, sub_project_id, task_title, task_description, task_status, task_start_date, task_end_date, task_duration, task_priority, task_note) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Beregn varighed ud fra datoerne
        task.recalculateDuration();

        jdbcTemplate.update(sql,
                employeeId,
                subProjectId,
                task.getTaskName(),
                task.getTaskDescription(),
                task.getTaskStatus().name(),   // <-- .name()
                task.getStartDate(),
                task.getEndDate(),
                task.getTaskDuration(),
                task.getTaskPriority().name(), // <-- .name()
                task.getTaskNote()
        );

    }

    public void deleteTask(long taskId) {
        jdbcTemplate.update("DELETE FROM task WHERE task_id = ?", taskId);
    }

    public void editTask(Task task) {
        String sql = "UPDATE task SET task_title = ?, task_description = ?, task_status = ?, task_start_date = ?, task_end_date = ?, task_duration = ?, task_priority = ?, task_note = ? WHERE task_id = ?";

        jdbcTemplate.update(sql,
                task.getTaskName(),
                task.getTaskDescription(),
                task.getTaskStatus().name(),  // <-- .name() er vigtigt
                task.getStartDate(),
                task.getEndDate(),
                task.getTaskDuration(),
                task.getTaskPriority().name(), // <-- .name() er vigtigt
                task.getTaskNote(),
                task.getTaskID()
        );
    }


    public Task getTaskById(long taskId) {
        String sql = "SELECT task_id, employee_id, sub_project_id, task_title, task_description, task_status, " +
                "task_start_date, task_end_date, task_duration, task_priority, task_note " +
                "FROM task WHERE task_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
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
        }, taskId);
    }

    public void saveSubTask(SubTask subTask, long subTaskId) {
        // Beregn varighed ud fra datoerne
        subTask.recalculateDuration();

        String sql = "INSERT INTO sub_task " +
                "(sub_task_id, task_id, sub_task_title, sub_task_description, sub_task_status, sub_task_start_date, " +
                "sub_task_end_date, sub_task_duration, sub_task_priority, sub_task_note) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";

        jdbcTemplate.update(sql,
                subTaskId,
                subTask.getSubTaskName(),
                subTask.getSubTaskDescription(),
                subTask.getSubTaskStatus(),
                subTask.getSubTaskStartDate(),
                subTask.getSubTaskEndDate(),
                subTask.getSubTaskDuration(),
                subTask.getSubTaskPriority(),
                subTask.getSubTaskNote()
        );
    }

    public void createSubTask(int employeeId, long taskId, String subTaskName, String subTaskDescription,
                              String subTaskStatus, LocalDate subTaskStartDate, LocalDate subTaskEndDate,
                              int subTaskDuration, String subTaskPriority, String subTaskNote) {

        String sql = "INSERT INTO sub_task (employee_id, task_id, sub_task_title, sub_task_description, " +
                "sub_task_status, sub_task_start_date, sub_task_end_date, sub_task_duration, " +
                "sub_task_priority, sub_task_note) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                employeeId,
                taskId,
                subTaskName,
                subTaskDescription,
                subTaskStatus,
                subTaskStartDate,
                subTaskEndDate,
                subTaskDuration,
                subTaskPriority,
                subTaskNote
        );
    }


    public List<SubTask> showSubTasksByTaskId(long taskId) {
        String sql = "SELECT sub_task_id, task_id, sub_task_title, sub_task_description, sub_task_status, " +
                "sub_task_start_date, sub_task_end_date, sub_task_duration, sub_task_priority, sub_task_note, employee_id " +
                "FROM sub_task WHERE task_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SubTask subTask = new SubTask();
            subTask.setSubTaskId(rs.getLong("sub_task_id"));
            subTask.setSubTaskName(rs.getString("sub_task_title"));
            subTask.setSubTaskDescription(rs.getString("sub_task_description"));
            subTask.setSubTaskStatus(Status.valueOf(rs.getString("sub_task_status")));
            subTask.setSubTaskNote(rs.getString("sub_task_note"));
            subTask.setSubTaskStartDate(rs.getObject("sub_task_start_date", java.time.LocalDate.class));
            subTask.setSubTaskEndDate(rs.getObject("sub_task_end_date", java.time.LocalDate.class));
            subTask.setSubTaskDuration(rs.getInt("sub_task_duration"));
            subTask.recalculateDuration();
            return subTask;
        }, taskId);
    }

    public void deleteSubTask(long subTaskId) {
        jdbcTemplate.update("DELETE FROM sub_task WHERE sub_task_id = ? ", subTaskId);
    }

    public void updateTaskNote(long taskId, String taskNote) {
        String sql = "UPDATE task SET task_note = ? WHERE task_id = ?";
        jdbcTemplate.update(sql, taskNote, taskId);
    }

    public void editSubTask(SubTask subTask) {
        subTask.recalculateDuration();

        String sql = "UPDATE sub_task SET " +
                "sub_task_title = ?, " +
                "sub_task_description = ?, " +
                "sub_task_status = ?, " +
                "sub_task_start_date = ?, " +
                "sub_task_end_date = ?, " +
                "sub_task_duration = ?, " +
                "sub_task_priority = ?, " +
                "sub_task_note = ? " +
                "WHERE sub_task_id = ?";

        int rowsAffected = jdbcTemplate.update(sql,
                subTask.getSubTaskName(),
                subTask.getSubTaskDescription(),
                subTask.getSubTaskStatus().name(),
                subTask.getSubTaskStartDate(),
                subTask.getSubTaskEndDate(),
                subTask.getSubTaskDuration(),
                subTask.getSubTaskPriority().name(),
                subTask.getSubTaskNote(),
                subTask.getSubTaskId()
        );

        System.out.println("Rows updated: " + rowsAffected);
    }

    public SubTask getSubTaskById(long subTaskId) {
        String sql = "SELECT sub_task_id, task_id, sub_task_title, sub_task_description, sub_task_status, " +
                "sub_task_start_date, sub_task_end_date, sub_task_duration, sub_task_priority, sub_task_note " +
                "FROM sub_task WHERE sub_task_id = ?";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            SubTask subTask = new SubTask();

            subTask.setSubTaskId(rs.getLong("sub_task_id"));
            subTask.setSubTaskName(rs.getString("sub_task_title"));
            subTask.setSubTaskDescription(rs.getString("sub_task_description"));
            subTask.setSubTaskStatus(Status.valueOf(rs.getString("sub_task_status")));
            subTask.setSubTaskStartDate(rs.getObject("sub_task_start_date", java.time.LocalDate.class));
            subTask.setSubTaskEndDate(rs.getObject("sub_task_end_date", java.time.LocalDate.class));
            subTask.setSubTaskDuration(rs.getInt("sub_task_duration"));
            subTask.setSubTaskPriority(Priority.valueOf(rs.getString("sub_task_priority")));
            subTask.setSubTaskNote(rs.getString("sub_task_note"));

            subTask.recalculateDuration();

            return subTask;
        }, subTaskId);
    }



    // TODO: subtask er afghængig af task, derfor erstat employeeId med taskId
    /*
    public List<SubTask> showSubTaskByTaskId(long taskId) {
        String sql = "SELECT task_id, sub_task_title, sub_task_description, sub_task_status," +
                "sub_task_start_date, sub_task_end_date, sub_task_duration, sub_task_priority, sub_task_note" +
                "FROM sub_task WHERE employee_id = ? ";

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
    } */
}



