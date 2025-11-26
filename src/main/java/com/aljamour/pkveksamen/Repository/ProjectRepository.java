package com.aljamour.pkveksamen.Repository;

import com.aljamour.pkveksamen.Model.Project;
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



//    public List<ProjectModel> findProjectsByUserID(long userId) {
//        String sql = "SELECT p.* FROM ProjectModel p INNER JOIN UserProjects up ON p.projectID = up.projectID WHERE up.userId = ?";
//    ;
//
//        return jdbcTemplate.query(sql, (rs, rowNum) -> new ProjectModel(
//                rs.getLong("projectID"),
//                rs.getString("projectName"),
//                rs.getString("projectDescription"),
//                rs.getDate("startDate").toLocalDate(),
//                rs.getDate("endDate").toLocalDate(),
//                rs.getString("projectCustomer"),
//                rs.getInt("projectDuration")
//        ), userId);
//    }

    public List<Project> showProjectsByUserID(long userID) {
        String sql = "SELECT project_id, project_title, project_description, project_start_date, project_end_date, project_costumer, project_duration " +
                "FROM PROJECT p " +
                "WHERE p.user_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Project(
                rs.getLong("project_id"),
                rs.getString("project_title"),
                rs.getString("project_description"),
                rs.getObject("project_start_date", LocalDate.class),
                rs.getObject("project_end_date",LocalDate.class),
                rs.getString("project_costumer"),
                rs.getInt("project_duration")
        ), userID);
    }

    public void saveProject(Project projectModel) {
        String sql = "INSERT INTO project (project_title, project_description, project_start_date, project_end_date, project_costomer, project_duration ) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                projectModel.getProjectName(),
                projectModel.getProjectDescription(),
                projectModel.getStartDate(),
                projectModel.getEndDate(),
                projectModel.getProjectCustomer(),
                projectModel.getProjectDuration()
        );
    }

    public void deleteProject(long projectID) {
        jdbcTemplate.update("DELETE FROM project WHERE project_id = ?", projectID);
    }

    public void editProject() {
    }
}
