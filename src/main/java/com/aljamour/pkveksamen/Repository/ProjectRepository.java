package com.aljamour.pkveksamen.Repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public class ProjectRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProjectRepository (JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createProject(String projectName, String projectDescription, LocalDate startDate,
                              LocalDate endDate,String projectCustomer, int projectDuration){
        jdbcTemplate.update("INSERT INTO project(project_title,project_description,project_start_date, project_end_date,project_duration,project_costumer) VALUES (?,?,?,?,?,?)",
        projectName,projectDescription,startDate,endDate,projectCustomer,projectDuration);
    }

}
