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
