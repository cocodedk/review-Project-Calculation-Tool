package com.aljamour.pkveksamen.Service;

import com.aljamour.pkveksamen.Model.ProjectModel;
import com.aljamour.pkveksamen.Repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

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

    public List<ProjectModel> getAllProjectList() {
        return projectRepository.getAllProjectList();
    }

    public List<ProjectModel> showProjectsByUserID(long userID) {
        return projectRepository.showProjectsByUserID(userID);
    }
}
