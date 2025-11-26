package com.aljamour.pkveksamen.Repository;

import com.aljamour.pkveksamen.Model.Task;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.springframework.jdbc.core.JdbcOperationsExtensionsKt.query;

public class TaskRepository {

    private final JdbcTemplate jdbcTemplate;

    public TaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }





    public List<Task> getTasksByTaskID(int taskID) {

        String sql = "SELECT task_id AS taskID," +
                "  task_description AS taskDescription" +
                "  task_status AS taskStatus" +
                "  task_start_date AS startDate" +
                "  task_end_date AS endDate" +
                "  task_duration AS duration" +
                "  task_priority AS taskPriority" +
                "  task_note AS taskNote" +
                "FROM task WHERE task_id = ?";
                return jdbcTemplate-query(sql, new BeanPropertyRowMapper<>(Task.class), taskID);

    }
}
