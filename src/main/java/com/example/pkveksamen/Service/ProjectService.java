package com.example.pkveksamen.Service;

import com.example.pkveksamen.Model.Project;
import com.example.pkveksamen.Model.SubProject;
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

    public List<SubProject> showSubProjectsByProjectId(long projectID){
        return projectRepository.showSubProjectsByProjectId(projectID);
    }

    public void saveProject(Project projectModel, int employeeId) {
        projectRepository.saveProject(projectModel, employeeId);
    }

    public void saveSubProject(SubProject subProject, long projectID){
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
}