package com.aljamour.pkveksamen.Controller;

import com.aljamour.pkveksamen.Model.Project;
import com.aljamour.pkveksamen.Model.User;
import com.aljamour.pkveksamen.Service.ProjectService;
import com.aljamour.pkveksamen.Service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("project")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    public ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @GetMapping("/list/{userId}")
    public String showProjectsByUserID(@PathVariable long userId, Model model){
        List<Project> projectList = projectService.showProjectsByUserID(userId);
        model.addAttribute("projectList", projectList);
        model.addAttribute("currentUserId", userId);

        User user = userService.getUserById(userId);
        if (user != null) {
            model.addAttribute("userName", user.getUserName());
            model.addAttribute("userRole", user.getRole());
        }
        return "project";
    }


    @GetMapping("/createproject/{userId}")
    public String showCreateform(@PathVariable long userId, Model model){
        model.addAttribute("project", new Project());
        model.addAttribute("currentUserId", userId);
        return "createproject";

    }

    @PostMapping("/saveproject/{userId}")
    public String saveProject(@PathVariable long userId, @ModelAttribute Project projectModel) {
        projectService.saveProject(projectModel, userId);
        return "redirect:/project/list/" + userId;
    }

    @PostMapping("/delete/{userId}/{id}")
    public String deleteProject(@PathVariable long userId, @PathVariable long id) {
        projectService.deleteProject(id);
        return "redirect:/project/list/" + userId;
    }
    // IK SLET DET TAK - ADEN KIGGER PÅ DET
//    @PostMapping("/edit")
//    public String editProject(@PathVariable String projectName,
//                              String projectDescription,
//                              LocalDate startDate,
//                              LocalDate endDate,
//                              String projectCustomer,
//                              int projectDuration, Model model){
//        projectService.editProject();
//        return "redirect:/project/list";
//    }
//


    @GetMapping("/edit/{userId}/{projectId}")
    public String showEditForm(@PathVariable long userId,
                               @PathVariable long projectId,
                               Model model) {
        Project project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);
        model.addAttribute("currentUserId", userId); // vigtigt for formularens action

        User user = userService.getUserById(userId);
        if (user != null) {
            model.addAttribute("userName", user.getUserName());
            model.addAttribute("userRole", user.getRole());
        }
        return "edit-project"; // din redigerings-view
    }

    @PostMapping("/edit/{userId}/{projectId}")
    public String editProject(@PathVariable long userId,
                              @PathVariable long projectId,
                              @ModelAttribute Project project) {

        project.setProjectID(projectId);
        projectService.editProject(project);

        return "redirect:/project/list/" + userId;
    }


    //  MODTAG FORMULAR OG GEM NYT PROJEKT
    @PostMapping("/create/{userId}")
    public String createProject(@PathVariable long userId,
                                @ModelAttribute Project project,
                                Model model) {

        projectService.createProject(
                project.getProjectName(),
                project.getProjectDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getProjectCustomer(),
                project.getProjectDuration(),
                userId
        );

        return "redirect:/project/list/" + userId; // tilbage til projektliste
    }
    @PostMapping("/project/createsubproject/{projectId}")
    public String createSubProject(@PathVariable long projectId, @ModelAttribute Project subProjectData) {
        Project project = projectService.getProjectById(projectId);

        // Sæt subproject-felter i eksisterende projekt
        project.setSubProjectName(subProjectData.getSubProjectName());
        project.setSubProjectDescription(subProjectData.getSubProjectDescription());
        project.setSubProjectStatus(subProjectData.getSubProjectStatus());
        project.setSubProjectDuration(subProjectData.getSubProjectDuration());

        projectService.saveProject(project);
        return "redirect:/projects";
    }

}

