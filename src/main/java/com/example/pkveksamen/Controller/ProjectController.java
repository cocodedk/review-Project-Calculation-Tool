package com.example.pkveksamen.Controller;

import com.example.pkveksamen.Model.Project;
import com.example.pkveksamen.Model.Employee;
import com.example.pkveksamen.Model.SubProject;
import com.example.pkveksamen.Service.ProjectService;
import com.example.pkveksamen.Service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@RequestMapping("project")
public class ProjectController {
    private final ProjectService projectService;
    private final EmployeeService employeeService;

    public ProjectController(ProjectService projectService, EmployeeService employeeService) {
        this.projectService = projectService;
        this.employeeService = employeeService;
    }

    @GetMapping("/list/{employeeId}")
    public String showProjectsByEmployeeId(@PathVariable int employeeId, Model model){
        List<Project> projectList = projectService.showProjectsByEmployeeId(employeeId);
        model.addAttribute("projectList", projectList);
        model.addAttribute("currentEmployeeId", employeeId);

        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee != null) {
            model.addAttribute("username", employee.getUsername());
            model.addAttribute("employeeRole", employee.getRole());
        }
        return "project";
    }

    @GetMapping("/subproject/list/{projectID}")
    public String showSubprojectByProjectId(@RequestParam("employeeId") int employeeId, 
                                         @PathVariable long projectID, 
                                         Model model){
        List<SubProject> subProjectList = projectService.showSubProjectsByProjectId(projectID);
        model.addAttribute("subProjectList", subProjectList);
        model.addAttribute("currentProjectId", projectID);
        model.addAttribute("currentEmployeeId", employeeId);
        
        // Add employee details for the header
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee != null) {
            model.addAttribute("username", employee.getUsername());
            model.addAttribute("employeeRole", employee.getRole());
        }

        return "subproject";
    }

    
    @GetMapping("/createproject/{employeeId}")
    public String showCreateProjectForm(@PathVariable int employeeId, Model model){
        model.addAttribute("project", new Project());
        model.addAttribute("currentEmployeeId", employeeId);
        return "createproject";
    }

    @GetMapping("/createsubproject/{employeeId}/{projectId}")
    public String showCreateSubProjectForm(@PathVariable int employeeId,
                                           @PathVariable long projectId,
                                           Model model){
        model.addAttribute("subProject", new SubProject());
        model.addAttribute("currentEmployeeId", employeeId);
        model.addAttribute("currentProjectId", projectId);
        return "createsubproject";
    }

    @PostMapping("/create/{employeeId}")
    public String createProject(@PathVariable int employeeId,
                                @ModelAttribute Project project,
                                Model model) {
        project.recalculateDuration();

        projectService.createProject(
                project.getProjectName(),
                project.getProjectDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getProjectCustomer(),
                employeeId
        );

        return "redirect:/project/list/" + employeeId;
    }

    @PostMapping("/saveproject/{employeeId}")
    public String saveProject(@PathVariable int employeeId, @ModelAttribute Project project) {
        project.recalculateDuration();
        projectService.saveProject(project, employeeId);
        return "redirect:/project/list/" + employeeId;
    }

    @PostMapping("/savesubproject/{employeeId}/{projectId}")
    public String saveSubProject(@PathVariable int employeeId,
                                 @PathVariable long projectId,
                                 @ModelAttribute SubProject subProject){
        subProject.recalculateDuration();
        projectService.saveSubProject(subProject, projectId);
        return "redirect:/project/subproject/list/" + projectId + "?employeeId=" + employeeId;
    }

    @PostMapping("/delete/{employeeId}/{id}")
    public String deleteProject(@PathVariable int employeeId, @PathVariable long id) {
        projectService.deleteProject(id);
        return "redirect:/project/list/" + employeeId;
    }

    @GetMapping("/edit/{employeeId}/{projectId}")
    public String showEditForm(@PathVariable int employeeId,
                               @PathVariable long projectId,
                               Model model) {
        Project project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);
        model.addAttribute("currentEmployeeId", employeeId);

        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee != null) {
            model.addAttribute("username", employee.getUsername());
            model.addAttribute("employeeRole", employee.getRole());
        }
        return "edit-project";
    }

    @PostMapping("/edit/{employeeId}/{projectId}")
    public String editProject(@PathVariable int employeeId,
                              @PathVariable long projectId,
                              @ModelAttribute Project project) {
        project.setProjectID(projectId);
        project.recalculateDuration();
        projectService.editProject(project);
        return "redirect:/project/list/" + employeeId;
    }

}