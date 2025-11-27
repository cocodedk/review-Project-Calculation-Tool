package com.aljamour.pkveksamen.Service;

import com.aljamour.pkveksamen.Model.Project;
import com.aljamour.pkveksamen.Repository.ProjectRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class ProjectService {

    private ProjectRepository projectRepository;

    public ProjectService (ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void createProject(String projectName, String projectDescription, LocalDate startDate,
                              LocalDate endDate, String projectCustomer, int projectDuration, long userId) {
        projectRepository.createProject(projectName, projectDescription, startDate, endDate, projectCustomer, projectDuration, userId);

    }

    public List<Project> showProjectsByUserID(long userID) {
        return projectRepository.showProjectsByUserID(userID);
    }

    public void saveProject(Project projectModel, long userId) {
        projectRepository.saveProject(projectModel, userId);
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
