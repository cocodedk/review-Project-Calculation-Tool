package com.aljamour.pkveksamen.Service;

import com.aljamour.pkveksamen.Repository.ProjectRepository;

public class ProjectService {

    private ProjectRepository projectRepository;

    public ProjectService (ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }
}
