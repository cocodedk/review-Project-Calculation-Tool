package com.example.pkveksamen.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.pkveksamen.Model.Project;

@Repository
public class ProjectRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createProject(String projectTitle, String projectDescription, LocalDate projectStartDate,
                              LocalDate projectEndDate, String projectCustomer, Integer employeeId) {
        jdbcTemplate.update(
                "INSERT INTO project (project_title, project_description, project_start_date, project_end_date, project_customer, employee_id) " +
                        "VALUES (?,?,?,?,?,?)",
                projectTitle,
                projectDescription,
                projectStartDate,
                projectEndDate,
                projectCustomer,
                employeeId
        );
    }

    public List<Project> showProjectsByEmployeeId(int employeeId) {
        String sql = "SELECT project_id, employee_id, project_title, project_description, project_start_date, project_end_date, project_customer " +
                "FROM project " +
                "WHERE employee_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Project project = new Project();
            project.setProjectID(rs.getLong("project_id"));
            project.setProjectName(rs.getString("project_title"));
            project.setProjectDescription(rs.getString("project_description"));
            project.setStartDate(rs.getObject("project_start_date", LocalDate.class));
            project.setEndDate(rs.getObject("project_end_date", LocalDate.class));
            project.setProjectCustomer(rs.getString("project_customer"));
            // Beregn varigheden automatisk
            project.recalculateDuration();
            return project;
        }, employeeId);
    }

    public void saveProject(Project projectModel, int employeeId) {
        String sql = "INSERT INTO project (project_title, project_description, project_start_date, project_end_date, project_customer, employee_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                projectModel.getProjectName(),
                projectModel.getProjectDescription(),
                projectModel.getStartDate(),
                projectModel.getEndDate(),
                projectModel.getProjectCustomer(),
                employeeId
        );
    }
//!
    public void deleteProject(long projectID) {
        jdbcTemplate.update("DELETE FROM project WHERE project_id = ?", projectID);
    }

    public void editProject(Project project) {
        String sql = "UPDATE project SET " +
                "project_title = ?, " +
                "project_description = ?, " +
                "project_start_date = ?, " +
                "project_end_date = ?, " +
                "project_customer = ? " +
                "WHERE project_id = ?";

        jdbcTemplate.update(sql,
                project.getProjectName(),
                project.getProjectDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getProjectCustomer(),
                project.getProjectID()
        );
    }

    public Project getProjectById(long projectId) {
        String sql = "SELECT project_id, employee_id, project_title, project_description, project_start_date, project_end_date, project_customer " +
                "FROM project " +
                "WHERE project_id = ?";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Project project = new Project();
            project.setProjectID(rs.getLong("project_id"));
            project.setProjectName(rs.getString("project_title"));
            project.setProjectDescription(rs.getString("project_description"));
            project.setStartDate(rs.getObject("project_start_date", LocalDate.class));
            project.setEndDate(rs.getObject("project_end_date", LocalDate.class));
            project.setProjectCustomer(rs.getString("project_customer"));
            // Beregn varigheden automatisk
            project.recalculateDuration();
            return project;
        }, projectId);
    }
}