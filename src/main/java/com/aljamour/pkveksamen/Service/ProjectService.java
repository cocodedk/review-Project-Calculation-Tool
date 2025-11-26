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
                              LocalDate endDate, String projectCustomer,  int projectDuration ){
        projectRepository.createProject(projectName,projectDescription,startDate,endDate,projectCustomer,projectDuration);

    }

    public List<Project> showProjectsByUserID(long userID) {
        return projectRepository.showProjectsByUserID(userID);
    }

    public void saveProject(Project projectModel) {
        projectRepository.saveProject(projectModel);
    }

    public void deleteProject(long projectID) {
        projectRepository.deleteProject(projectID);
    }

//    public void editProject() {
//        projectRepository.editProject()
//    }
}
