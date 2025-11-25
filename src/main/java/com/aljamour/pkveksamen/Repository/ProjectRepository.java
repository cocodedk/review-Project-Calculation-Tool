package com.aljamour.pkveksamen.Repository;

import com.aljamour.pkveksamen.Model.ProjectModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

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

    public List<ProjectModel> getAllProjectList() {
        String sql = "SELECT projectID, project_title, project_description, project_start_date, project_end_date, project_costumer, project_duration FROM project";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new ProjectModel(
                rs.getLong("project_id"),
                rs.getString("project_title"),
                rs.getString("project_description"),
                rs.getObject("project_start_date", LocalDate.class),
                rs.getObject("project_end_date",LocalDate.class),
                rs.getString("project_costumer"),
                rs.getInt("project_duration")
                ));

    }

    public List<ProjectModel> showProjectsByUserID(long userID) {
        String sql = "SELECT p.* FROM project w INNER JOIN "
    }
}
