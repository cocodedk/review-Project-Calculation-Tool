package com.example.pkveksamen.repository;

import java.time.LocalDate;
import java.util.List;

import com.example.pkveksamen.model.Employee;
import com.example.pkveksamen.model.EmployeeRole;
import com.example.pkveksamen.model.SubProject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.pkveksamen.model.Project;

@Repository
public class ProjectRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createProject(String projectTitle, String projectDescription, LocalDate projectStartDate,
                              LocalDate projectDeadline, String projectCustomer, Integer employeeId) {
        jdbcTemplate.update(
                "INSERT INTO project (project_title, project_description, project_start_date, project_deadline, project_customer, employee_id) " +
                        "VALUES (?,?,?,?,?,?)",
                projectTitle,
                projectDescription,
                projectStartDate,
                projectDeadline,
                projectCustomer,
                employeeId
        );
    }

    public List<Project> showProjectsByEmployeeId(int employeeId) {
        String sql = "SELECT DISTINCT p.project_id, p.employee_id, p.project_title, p.project_description, p.project_start_date, p.project_deadline, p.project_customer " +
                "FROM project p " +
                "WHERE p.employee_id = ? " +
                "UNION " +
                "SELECT DISTINCT p.project_id, p.employee_id, p.project_title, p.project_description, p.project_start_date, p.project_deadline, p.project_customer " +
                "FROM project p " +
                "INNER JOIN project_employee pe ON p.project_id = pe.project_id " +
                "WHERE pe.employee_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Project project = new Project();
            project.setProjectID(rs.getLong("project_id"));
            project.setProjectName(rs.getString("project_title"));
            project.setProjectDescription(rs.getString("project_description"));
            project.setProjectStartDate(rs.getObject("project_start_date", LocalDate.class));
            project.setProjectDeadline(rs.getObject("project_deadline", LocalDate.class));
            project.setProjectCustomer(rs.getString("project_customer"));
            project.recalculateDuration();
            return project;
        }, employeeId, employeeId);
    }

    public List<SubProject> showSubProjectsByProjectId(long projectID) {
        String sql = "SELECT sub_project_id, project_id, sub_project_title, sub_project_description, sub_project_start_date, sub_project_deadline, sub_project_duration " +
                "FROM sub_project " +
                "WHERE project_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SubProject subProject = new SubProject();
            subProject.setSubProjectID(rs.getLong("sub_project_id"));
            subProject.setSubProjectName(rs.getString("sub_project_title"));
            subProject.setSubProjectDescription(rs.getString("sub_project_description"));
            subProject.setSubProjectStartDate(rs.getObject("sub_project_start_date", LocalDate.class));
            subProject.setSubProjectDeadline(rs.getObject("sub_project_deadline", LocalDate.class));
            subProject.setSubProjectDuration(rs.getInt("sub_project_duration"));
            // Beregn varigheden automatisk
            subProject.recalculateDuration();
            return subProject;
        }, projectID);
    }


    public void saveProject(Project project, int employeeId) {
        String sql = "INSERT INTO project (project_title, project_description, project_start_date, project_deadline, project_customer, employee_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                project.getProjectName(),
                project.getProjectDescription(),
                project.getProjectStartDate(),
                project.getProjectDeadline(),
                project.getProjectCustomer(),
                employeeId
        );
    }

    public void saveSubProject(SubProject subProject, long projectID) {
        // Beregn varighed ud fra datoerne
        subProject.recalculateDuration();

        String sql = "INSERT INTO sub_project " +
                "(project_id, sub_project_title, sub_project_description, sub_project_start_date, sub_project_deadline, sub_project_duration) " +
                "VALUES (?,?,?,?,?,?)";

        jdbcTemplate.update(sql,
                projectID,                             // project_id
                subProject.getSubProjectName(),        // sub_project_title
                subProject.getSubProjectDescription(), // sub_project_description
                subProject.getSubProjectStartDate(),             // sub_project_start_date
                subProject.getSubProjectDeadline(),               // sub_project_deadline
                subProject.getSubProjectDuration()     // sub_project_duration
        );
    }

    public void deleteProject(long projectID) {
        // First delete all subtasks related to tasks in subprojects of this project
        String deleteSubtasksSql = "DELETE FROM sub_task WHERE task_id IN " +
                "(SELECT t.task_id FROM task t " +
                "JOIN sub_project sp ON t.sub_project_id = sp.sub_project_id " +
                "WHERE sp.project_id = ?)";
        jdbcTemplate.update(deleteSubtasksSql, projectID);
        // Then delete all tasks in subprojects of this project
        String deleteTasksSql = "DELETE FROM task WHERE sub_project_id IN " +
                "(SELECT sub_project_id FROM sub_project WHERE project_id = ?)";
        jdbcTemplate.update(deleteTasksSql, projectID);
        // Then delete all subprojects of this project
        String deleteSubProjectsSql = "DELETE FROM sub_project WHERE project_id = ?";
        jdbcTemplate.update(deleteSubProjectsSql, projectID);
        // Finally, delete the project itself
        jdbcTemplate.update("DELETE FROM project WHERE project_id = ?", projectID);
    }

    public void editProject(Project project) {
        String sql = "UPDATE project SET " +
                "project_title = ?, " +
                "project_description = ?, " +
                "project_start_date = ?, " +
                "project_deadline = ?, " +
                "project_customer = ? " +
                "WHERE project_id = ?";

        jdbcTemplate.update(sql,
                project.getProjectName(),
                project.getProjectDescription(),
                project.getProjectStartDate(),
                project.getProjectDeadline(),
                project.getProjectCustomer(),
                project.getProjectID()
        );
    }
    public void editSubProject(SubProject subProject) {
        String sql = "UPDATE sub_project SET " +
                "sub_project_title = ?, " +
                "sub_project_description = ?, " +
                "sub_project_start_date = ?, " +
                "sub_project_deadline = ?, " +
                "sub_project_duration = ? " +
                "WHERE sub_project_id = ?";

        jdbcTemplate.update(sql,
                subProject.getSubProjectName(),
                subProject.getSubProjectDescription(),
                subProject.getSubProjectStartDate(),
                subProject.getSubProjectDeadline(),
                subProject.getSubProjectDuration(),
                subProject.getSubProjectID()
        );
    }

    public Project getProjectById(long projectId) {
        String sql = "SELECT project_id, employee_id, project_title, project_description, project_start_date, project_deadline, project_customer " +
                "FROM project " +
                "WHERE project_id = ?";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Project project = new Project();
            project.setProjectID(rs.getLong("project_id"));
            project.setProjectName(rs.getString("project_title"));
            project.setProjectDescription(rs.getString("project_description"));
            project.setProjectStartDate(rs.getObject("project_start_date", LocalDate.class));
            project.setProjectDeadline(rs.getObject("project_deadline", LocalDate.class));
            project.setProjectCustomer(rs.getString("project_customer"));
            // Beregn varigheden automatisk
            project.recalculateDuration();
            return project;
        }, projectId);
    }


    public SubProject getSubProjectBySubProjectID(long subProjectID) {
        String sql = "SELECT sub_project_id, project_id, sub_project_title, sub_project_description, sub_project_start_date, sub_project_deadline, sub_project_duration " +
                "FROM sub_project " +
                "WHERE sub_project_id = ?";


        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            SubProject subproject = new SubProject();
            subproject.setSubProjectID(rs.getLong("sub_project_id"));
            subproject.setSubProjectName(rs.getString("sub_project_title"));
            subproject.setSubProjectDescription(rs.getString("sub_project_description"));
            subproject.setSubProjectStartDate(rs.getObject("sub_project_start_date", LocalDate.class));
            subproject.setSubProjectDeadline(rs.getObject("sub_project_deadline", LocalDate.class));
            subproject.setSubProjectDuration(rs.getInt("sub_project_duration"));
            // Beregn varigheden automatisk
            subproject.recalculateDuration();
            return subproject;
        }, subProjectID);
    }

    public void deleteSubProject(long subProjectId) {
        jdbcTemplate.update("DELETE FROM sub_project WHERE sub_project_id = ?", subProjectId);
    }

    public List<Employee> getProjectMembers(long projectId) {
        String sql = "SELECT DISTINCT e.employee_id, e.username, e.email, e.role " +
                "FROM employee e " +
                "INNER JOIN project p ON e.employee_id = p.employee_id " +
                "WHERE p.project_id = ? " +
                "UNION " +
                "SELECT DISTINCT e.employee_id, e.username, e.email, e.role " +
                "FROM employee e " +
                "INNER JOIN project_employee pe ON e.employee_id = pe.employee_id " +
                "WHERE pe.project_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Employee employee = new Employee();
            employee.setEmployeeId(rs.getInt("employee_id"));
            employee.setUsername(rs.getString("username"));
            employee.setEmail(rs.getString("email"));
            String roleStr = rs.getString("role");
            if (roleStr != null) {
                employee.setRole(EmployeeRole.fromDisplayName(roleStr));
            }
            return employee;
        }, projectId, projectId);
    }

    public List<Employee> getAvailableEmployeesToAdd(long projectId) {
        String sql = "SELECT e.employee_id, e.username, e.email, e.role " +
                "FROM employee e " +
                "WHERE e.employee_id NOT IN (" +
                "    SELECT DISTINCT p.employee_id FROM project p WHERE p.project_id = ? " +
                "    UNION " +
                "    SELECT DISTINCT pe.employee_id FROM project_employee pe WHERE pe.project_id = ?" +
                ")";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Employee employee = new Employee();
            employee.setEmployeeId(rs.getInt("employee_id"));
            employee.setUsername(rs.getString("username"));
            employee.setEmail(rs.getString("email"));
            String roleStr = rs.getString("role");
            if (roleStr != null) {
                employee.setRole(EmployeeRole.fromDisplayName(roleStr));
            }
            return employee;
        }, projectId, projectId);
    }

    public void addEmployeeToProject(int employeeId, long projectId) {
        String sql = "INSERT INTO project_employee (employee_id, project_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, employeeId, projectId);
    }


    public void removeEmployeeFromProject(int employeeId, long projectId) {
        String sql = "DELETE FROM project_employee WHERE project_id = ? AND employee_id = ?";
        jdbcTemplate.update(sql, projectId, employeeId);
    }
}
