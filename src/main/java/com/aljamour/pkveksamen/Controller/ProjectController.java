package com.aljamour.pkveksamen.Controller;

import com.aljamour.pkveksamen.Model.Project;
import com.aljamour.pkveksamen.Service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("project")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // I ProjectController.java
    @GetMapping("/list/{userId}")
    public String showProjectsByUserID(@PathVariable long userId, Model model){
        List<Project> projectList = projectService.showProjectsByUserID(userId);
        model.addAttribute("projectList", projectList);
        model.addAttribute("currentUserId", userId);
        return "project";
    }

    @GetMapping("createproject")
    public String showCreateform(Model model){
        model.addAttribute("project", new Project());
        return "createproject";

    }

    @PostMapping("createproject")
    public String createProject(@RequestParam String projectName, @RequestParam String projectDescription, @RequestParam LocalDate startDate,
                                @RequestParam LocalDate endDate, @RequestParam String projectCustomer, @RequestParam int projectDuration ){
        projectService.createProject(projectName,projectDescription,startDate,endDate,projectCustomer,projectDuration);
        return "redirect:/project";
    }


    @PostMapping("/saveproject")
    public String saveProject(@ModelAttribute Project projectModel) {
        projectService.saveProject(projectModel);
        return "redirect:/project";
    }

    @PostMapping("/delete/{id}")
    public String deleteProject(@PathVariable long id) {
        projectService.deleteProject(id);
        return "redirect:/project";
    }

//    @PostMapping("/edit")
//    public String editProject(@PathVariable String projectName,
//                              String projectDescription,
//                              LocalDate startDate,
//                              LocalDate endDate,
//                              String projectCustomer,
//                              int projectDuration, Model model){
//        projectService.editProject();
//        return "redirect:/project";
//    }





}
