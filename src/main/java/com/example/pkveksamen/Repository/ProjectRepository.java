package com.example.pkveksamen.Repository;

import java.time.LocalDate;
import java.util.List;

import com.example.pkveksamen.Model.SubProject;
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

    public List<SubProject> showSubProjectsByProjectId(long projectID) {
        String sql = "SELECT sub_project_id, project_id, sub_project_title, sub_project_description, sub_project_start_date, sub_project_end_date, sub_project_duration " +
                "FROM sub_project " +
                "WHERE project_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SubProject subProject = new SubProject();
            subProject.setSubProjectID(rs.getLong("sub_project_id"));
            subProject.setSubProjectName(rs.getString("sub_project_title"));
            subProject.setSubProjectDescription(rs.getString("sub_project_description"));
            subProject.setStartDate(rs.getObject("sub_project_start_date", LocalDate.class));
            subProject.setEndDate(rs.getObject("sub_project_end_date", LocalDate.class));
            subProject.setSubProjectDuration(rs.getInt("sub_project_duration"));
            // Beregn varigheden automatisk
            subProject.recalculateDuration();
            return subProject;
        }, projectID);
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

    public void saveSubProject(SubProject subProject, long projectID) {
        // Beregn varighed ud fra datoerne
        subProject.recalculateDuration();

        String sql = "INSERT INTO sub_project " +
                "(project_id, sub_project_title, sub_project_description, sub_project_start_date, sub_project_end_date, sub_project_duration) " +
                "VALUES (?,?,?,?,?,?)";

        jdbcTemplate.update(sql,
                projectID,                             // project_id
                subProject.getSubProjectName(),        // sub_project_title
                subProject.getSubProjectDescription(), // sub_project_description
                subProject.getStartDate(),             // sub_project_start_date
                subProject.getEndDate(),               // sub_project_end_date
                subProject.getSubProjectDuration()     // sub_project_duration
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
    public void editSubProject(SubProject subProject) {
        String sql = "UPDATE sub_project SET " +
                "sub_project_title = ?, " +
                "sub_project_description = ?, " +
                "sub_project_start_date = ?, " +
                "sub_project_end_date = ?, " +
                "sub_project_duration = ? " +
                "WHERE sub_project_id = ?";

        jdbcTemplate.update(sql,
                subProject.getSubProjectName(),
                subProject.getSubProjectDescription(),
                subProject.getStartDate(),
                subProject.getEndDate(),
                subProject.getSubProjectDuration(),
                subProject.getSubProjectID()
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


    public SubProject getSubProjectByID(long subProjectID) {
        String sql = "SELECT sub_project_id, project_id, sub_project_title, sub_project_description, sub_project_start_date, sub_project_end_date, sub_project_duration " +
                "FROM sub_project " +
                "WHERE sub_project_id = ?";


        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            SubProject subproject = new SubProject();
            subproject.setSubProjectID(rs.getLong("sub_project_id"));
            subproject.setSubProjectName(rs.getString("sub_project_title"));
            subproject.setSubProjectDescription(rs.getString("sub_project_description"));
            subproject.setStartDate(rs.getObject("sub_project_start_date", LocalDate.class));
            subproject.setEndDate(rs.getObject("sub_project_end_date", LocalDate.class));
            subproject.setSubProjectDuration(rs.getInt("sub_project_duration"));
            // Beregn varigheden automatisk
            subproject.recalculateDuration();
            return subproject;
        }, subProjectID);
    }




    public void deleteSubProject(long subProjectId) {
        jdbcTemplate.update("DELETE FROM sub_project WHERE sub_project_id = ?", subProjectId);
    }


}

