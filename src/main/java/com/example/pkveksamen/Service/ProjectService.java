package com.example.pkveksamen.Service;

import com.example.pkveksamen.Model.Project;
import com.example.pkveksamen.Repository.ProjectRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class ProjectService {

    private ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void createProject(String projectTitle, String projectDescription, LocalDate projectStartDate,
                              LocalDate projectEndDate, String projectCustomer, Integer employeeId) {
        projectRepository.createProject(projectTitle, projectDescription, projectStartDate, projectEndDate, projectCustomer, employeeId);
    }

    public List<Project> showProjectsByEmployeeId(int employeeId) {
        return projectRepository.showProjectsByEmployeeId(employeeId);
    }

    public void saveProject(Project projectModel, int employeeId) {
        projectRepository.saveProject(projectModel, employeeId);
    }

    public void deleteProject(long projectID) {
        projectRepository.deleteProject(projectID);
    }

    public void editProject(Project project) {
        projectRepository.editProject(project);
    }

    public Project getProjectById(long projectId) {
        return projectRepository.getProjectById(projectId);
    }
}