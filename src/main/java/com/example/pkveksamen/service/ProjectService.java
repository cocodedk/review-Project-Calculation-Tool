package com.example.pkveksamen.service;

import com.example.pkveksamen.model.Project;
import com.example.pkveksamen.model.SubProject;
import com.example.pkveksamen.model.Task;
import com.example.pkveksamen.repository.ProjectRepository;
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

    public List<SubProject> showSubProjectsByProjectId(long projectID) {
        return projectRepository.showSubProjectsByProjectId(projectID);
    }

    public void saveProject(Project projectModel, int employeeId) {
        projectRepository.saveProject(projectModel, employeeId);
    }

    public void saveSubProject(SubProject subProject, long projectID) {
        projectRepository.saveSubProject(subProject, projectID);
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

    public SubProject getSubProjectBySubProjectID(long subProjectID) {
       return projectRepository.getSubProjectBySubProjectID(subProjectID);
    }

    public void editSubProject(SubProject subProject) {
        projectRepository.editSubProject(subProject);
    }
    public void deleteSubProject(long subProjectId) {
        projectRepository.deleteSubProject(subProjectId);
    }
}