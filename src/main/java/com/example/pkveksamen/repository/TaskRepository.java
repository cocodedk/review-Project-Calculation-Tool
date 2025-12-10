package com.example.pkveksamen.repository;

import com.example.pkveksamen.model.Employee;
import com.example.pkveksamen.model.EmployeeRole;
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

    public void createTask(Integer employeeId, long subProjectId, String taskName, String taskDescription,
                           Status status, LocalDate taskStartDate, LocalDate taskDeadline, int taskDuration,
                           Priority priority, String taskNote) {

        jdbcTemplate.update(
                "INSERT INTO task (employee_id, sub_project_id, task_title, task_description, task_status, " +
                        "task_start_date, task_deadline, task_duration, task_priority, task_note) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                employeeId,
                subProjectId,
                taskName,
                taskDescription,
                status.getDisplayName(),
                taskStartDate,
                taskDeadline,
                taskDuration,
                priority.getDisplayName(),
                taskNote
        );
    }

    public List<Task> showTaskByEmployeeId(int employeeId) {
        String sql = "SELECT t.task_id, t.employee_id, t.sub_project_id, t.task_title, t.task_description, t.task_status, " +
                "t.task_start_date, t.task_deadline, t.task_duration, t.task_priority, t.task_note, " +
                "e.employee_id as assigned_employee_id, e.username, e.email, e.role " +
                "FROM task t " +
                "LEFT JOIN employee e ON t.employee_id = e.employee_id " +
                "WHERE t.employee_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Task task = new Task();
            task.setTaskID(rs.getInt("task_id"));
            task.setTaskName(rs.getString("task_title"));
            task.setTaskDescription(rs.getString("task_description"));
            task.setTaskStatus(Status.fromDisplayName(rs.getString("task_status")));
            task.setTaskNote(rs.getString("task_note"));
            task.setTaskStartDate(rs.getObject("task_start_date", LocalDate.class));
            task.setTaskDeadline(rs.getObject("task_deadline", LocalDate.class));
            task.setTaskDuration(rs.getInt("task_duration"));
            task.recalculateDuration();
            
            if (rs.getObject("assigned_employee_id") != null) {
                Employee employee = new Employee();
                employee.setEmployeeId(rs.getInt("assigned_employee_id"));
                employee.setUsername(rs.getString("username"));
                employee.setEmail(rs.getString("email"));
                String roleStr = rs.getString("role");
                if (roleStr != null) {
                    employee.setRole(EmployeeRole.fromDisplayName(roleStr));
                }
                task.setAssignedEmployee(employee);
            }
            
            return task;
        }, employeeId);
    }

    public List<Task> showTasksBySubProjectId(long subProjectId) {
        String sql = "SELECT t.task_id, t.employee_id, t.sub_project_id, t.task_title, t.task_description, t.task_status, " +
                "t.task_start_date, t.task_deadline, t.task_duration, t.task_priority, t.task_note, " +
                "e.employee_id as assigned_employee_id, e.username, e.email, e.role " +
                "FROM task t " +
                "LEFT JOIN employee e ON t.employee_id = e.employee_id " +
                "WHERE t.sub_project_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Task task = new Task();
            task.setTaskID(rs.getInt("task_id"));
            task.setTaskName(rs.getString("task_title"));
            task.setTaskDescription(rs.getString("task_description"));
            task.setTaskStatus(Status.fromDisplayName(rs.getString("task_status")));
            task.setTaskNote(rs.getString("task_note"));
            task.setTaskStartDate(rs.getObject("task_start_date", LocalDate.class));
            task.setTaskDeadline(rs.getObject("task_deadline", LocalDate.class));
            task.setTaskDuration(rs.getInt("task_duration"));
            String priorityStr = rs.getString("task_priority");
            if (priorityStr != null) {
                task.setTaskPriority(Priority.fromDisplayName(priorityStr));
            }
            task.recalculateDuration();
            
            if (rs.getObject("assigned_employee_id") != null) {
                Employee employee = new Employee();
                employee.setEmployeeId(rs.getInt("assigned_employee_id"));
                employee.setUsername(rs.getString("username"));
                employee.setEmail(rs.getString("email"));
                String roleStr = rs.getString("role");
                if (roleStr != null) {
                    employee.setRole(EmployeeRole.fromDisplayName(roleStr));
                }
                task.setAssignedEmployee(employee);
            }
            
            return task;
        }, subProjectId);
    }

    public void saveTask(Task task, int employeeId, long projectId, long subProjectId) {
        String sql = "INSERT INTO task (employee_id, sub_project_id, task_title, task_description, task_status, task_start_date, task_deadline, task_duration, task_priority, task_note) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        task.recalculateDuration();
        jdbcTemplate.update(sql,
                employeeId,
                subProjectId,
                task.getTaskName(),
                task.getTaskDescription(),
                task.getTaskStatus().name(),
                task.getTaskStartDate(),
                task.getTaskDeadline(),
                task.getTaskDuration(),
                task.getTaskPriority().name(),
                task.getTaskNote()
        );
    }

    public void deleteTask(long taskId) {
        jdbcTemplate.update("DELETE FROM task WHERE task_id = ?", taskId);
    }

    public void editTask(Task task) {
        String sql = "UPDATE task SET task_title = ?, task_description = ?, task_status = ?, task_start_date = ?, task_deadline = ?, task_duration = ?, task_priority = ?, task_note = ?, employee_id = ? WHERE task_id = ?";
        Integer employeeId = task.getAssignedEmployee() != null ? task.getAssignedEmployee().getEmployeeId() : null;
        jdbcTemplate.update(sql,
                task.getTaskName(),
                task.getTaskDescription(),
                task.getTaskStatus().name(),
                task.getTaskStartDate(),
                task.getTaskDeadline(),
                task.getTaskDuration(),
                task.getTaskPriority().name(),
                task.getTaskNote(),
                employeeId,
                task.getTaskID()
        );
    }

    public Task getTaskById(long taskId) {
        String sql = "SELECT t.task_id, t.employee_id, t.sub_project_id, t.task_title, t.task_description, t.task_status, " +
                "t.task_start_date, t.task_deadline, t.task_duration, t.task_priority, t.task_note, " +
                "e.employee_id as assigned_employee_id, e.username, e.email, e.role " +
                "FROM task t " +
                "LEFT JOIN employee e ON t.employee_id = e.employee_id " +
                "WHERE t.task_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Task task = new Task();
            task.setTaskID(rs.getInt("task_id"));
            task.setTaskName(rs.getString("task_title"));
            task.setTaskDescription(rs.getString("task_description"));
            task.setTaskStatus(Status.fromDisplayName(rs.getString("task_status")));
            task.setTaskNote(rs.getString("task_note"));
            task.setTaskStartDate(rs.getObject("task_start_date", LocalDate.class));
            task.setTaskDeadline(rs.getObject("task_deadline", LocalDate.class));
            task.setTaskDuration(rs.getInt("task_duration"));
            String priorityStr = rs.getString("task_priority");
            if (priorityStr != null) {
                task.setTaskPriority(Priority.fromDisplayName(priorityStr));
            }
            task.recalculateDuration();
            
            if (rs.getObject("assigned_employee_id") != null) {
                Employee employee = new Employee();
                employee.setEmployeeId(rs.getInt("assigned_employee_id"));
                employee.setUsername(rs.getString("username"));
                employee.setEmail(rs.getString("email"));
                String roleStr = rs.getString("role");
                if (roleStr != null) {
                    employee.setRole(EmployeeRole.fromDisplayName(roleStr));
                }
                task.setAssignedEmployee(employee);
            }
            
            return task;
        }, taskId);
    }

    public void saveSubTask(SubTask subTask, long subTaskId) {
        subTask.recalculateDuration();
        String sql = "INSERT INTO sub_task (sub_task_id, task_id, sub_task_title, sub_task_description," +
                " sub_task_status, sub_task_start_date, sub_task_deadline, sub_task_duration, sub_task_priority, sub_task_note) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql,
                subTaskId,
                subTask.getSubTaskName(),
                subTask.getSubTaskDescription(),
                subTask.getSubTaskStatus().name(),
                subTask.getSubTaskStartDate(),
                subTask.getSubTaskDeadline(),
                subTask.getSubTaskDuration(),
                subTask.getSubTaskPriority().name(),
                subTask.getSubTaskNote()
        );
    }

    public void createSubTask(long taskId, String subTaskName, String subTaskDescription,
                              String subTaskStatus, LocalDate subTaskStartDate, LocalDate subTaskDeadline,
                              int subTaskDuration, String subTaskPriority, String subTaskNote) {

        String sql = "INSERT INTO sub_task (task_id, sub_task_title, sub_task_description, " +
                "sub_task_status, sub_task_start_date, sub_task_deadline, sub_task_duration, sub_task_priority, sub_task_note) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                taskId,
                subTaskName,
                subTaskDescription,
                subTaskStatus,
                subTaskStartDate,
                subTaskDeadline,
                subTaskDuration,
                subTaskPriority,
                subTaskNote
        );
    }

    public List<SubTask> showSubTasksByTaskId(long taskId) {
        String sql = "SELECT sub_task_id, task_id, sub_task_title, sub_task_description, sub_task_status, " +
                "sub_task_start_date, sub_task_deadline, sub_task_duration, sub_task_priority, sub_task_note " +
                "FROM sub_task WHERE task_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SubTask subTask = new SubTask();
            subTask.setSubTaskId(rs.getLong("sub_task_id"));
            subTask.setSubTaskName(rs.getString("sub_task_title"));
            subTask.setSubTaskDescription(rs.getString("sub_task_description"));
            String statusStr = rs.getString("sub_task_status");
            if (statusStr != null) {
                subTask.setSubTaskStatus(Status.fromDisplayName(statusStr));
            }
            subTask.setSubTaskNote(rs.getString("sub_task_note"));
            subTask.setSubTaskStartDate(rs.getObject("sub_task_start_date", LocalDate.class));
            subTask.setSubTaskDeadline(rs.getObject("sub_task_deadline", LocalDate.class));
            subTask.setSubTaskDuration(rs.getInt("sub_task_duration"));
            String priorityStr = rs.getString("sub_task_priority");
            if (priorityStr != null) {
                subTask.setSubTaskPriority(Priority.fromDisplayName(priorityStr));
            }
            subTask.recalculateDuration();
            return subTask;
        }, taskId);
    }

    public void deleteSubTask(long subTaskId) {
        jdbcTemplate.update("DELETE FROM sub_task WHERE sub_task_id = ?", subTaskId);
    }

    public void updateTaskNote(long taskId, String taskNote) {
        String sql = "UPDATE task SET task_note = ? WHERE task_id = ?";
        jdbcTemplate.update(sql, taskNote, taskId);
    }

    public void updateTaskStatus(long taskId, String taskStatus) {
        String sql = "UPDATE task SET task_status = ? WHERE task_id = ?";
        jdbcTemplate.update(sql, taskStatus, taskId);
    }

    public void updateTaskPriority(long taskId, String taskPriority) {
        String sql = "UPDATE task SET task_priority = ? WHERE task_id = ?";
        jdbcTemplate.update(sql, taskPriority, taskId);
    }

    public void updateSubTaskStatus(long subTaskId, String subTaskStatus) {
        String sql = "UPDATE sub_task SET sub_task_status = ? WHERE sub_task_id = ?";
        jdbcTemplate.update(sql, subTaskStatus, subTaskId);
    }

    public void updateSubTaskPriority(long subTaskId, String subTaskPriority) {
        String sql = "UPDATE sub_task SET sub_task_priority = ? WHERE sub_task_id = ?";
        jdbcTemplate.update(sql, subTaskPriority, subTaskId);
    }

    public void updateSubTaskNote(long subTaskId, String subTaskNote) {
        String sql = "UPDATE sub_task SET sub_task_note = ? WHERE sub_task_id = ?";
        jdbcTemplate.update(sql, subTaskNote, subTaskId);
    }



    public void editSubTask(SubTask subTask) {
        subTask.recalculateDuration();
        String sql = "UPDATE sub_task SET sub_task_title = ?, sub_task_description = ?, sub_task_status = ?, " +
                "sub_task_start_date = ?, sub_task_deadline = ?, sub_task_duration = ?, sub_task_priority = ?, " +
                "sub_task_note = ? WHERE sub_task_id = ?";
        jdbcTemplate.update(sql,
                subTask.getSubTaskName(),
                subTask.getSubTaskDescription(),
                subTask.getSubTaskStatus().name(),
                subTask.getSubTaskStartDate(),
                subTask.getSubTaskDeadline(),
                subTask.getSubTaskDuration(),
                subTask.getSubTaskPriority().name(),
                subTask.getSubTaskNote(),
                subTask.getSubTaskId()
        );
    }

    public SubTask getSubTaskById(long subTaskId) {
        String sql = "SELECT sub_task_id, task_id, sub_task_title, sub_task_description, sub_task_status, " +
                "sub_task_start_date, sub_task_deadline, sub_task_duration, sub_task_priority, sub_task_note " +
                "FROM sub_task WHERE sub_task_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            SubTask subTask = new SubTask();
            subTask.setSubTaskId(rs.getLong("sub_task_id"));
            subTask.setSubTaskName(rs.getString("sub_task_title"));
            subTask.setSubTaskDescription(rs.getString("sub_task_description"));
            subTask.setSubTaskStatus(Status.fromDisplayName(rs.getString("sub_task_status")));
            subTask.setSubTaskStartDate(rs.getObject("sub_task_start_date", LocalDate.class));
            subTask.setSubTaskDeadline(rs.getObject("sub_task_deadline", LocalDate.class));
            subTask.setSubTaskDuration(rs.getInt("sub_task_duration"));
            String subTaskPriorityStr = rs.getString("sub_task_priority");
            if (subTaskPriorityStr != null) {
                subTask.setSubTaskPriority(Priority.fromDisplayName(subTaskPriorityStr));
            }
            subTask.setSubTaskNote(rs.getString("sub_task_note"));
            subTask.recalculateDuration();
            return subTask;
        }, subTaskId);
    }
}
