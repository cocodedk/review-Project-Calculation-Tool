package com.example.pkveksamen.controller;

import com.example.pkveksamen.model.EmployeeRole;
import com.example.pkveksamen.model.Project;
import com.example.pkveksamen.model.Employee;
import com.example.pkveksamen.model.SubProject;
import com.example.pkveksamen.service.ProjectService;
import com.example.pkveksamen.service.EmployeeService;
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

    // her laver vi metoderene på hvad de forskellig bruger skal kunne.
    public boolean isManager(Employee employee){
        return employee != null && employee.getRole() == EmployeeRole.PROJECT_MANAGER;
    }

    public boolean isTeamMember(Employee employee){
        return employee != null && employee.getRole() == EmployeeRole.TEAM_MEMBER;
    }

    @GetMapping("/list/{employeeId}")
    public String showProjectsByEmployeeId(@PathVariable int employeeId, Model model) {
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
                                            Model model) {
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
    public String showCreateProjectForm(@PathVariable int employeeId, Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("currentEmployeeId", employeeId);
//        List<Employee> teamMembers = projectService.getAllTeamMembers();
//        model.addAttribute("teamMembers", teamMembers);
        return "createproject";
    }

    @GetMapping("/createsubproject/{employeeId}/{projectId}")
    public String showCreateSubProjectForm(@PathVariable int employeeId,
                                           @PathVariable long projectId,
                                           Model model) {
        model.addAttribute("subProject", new SubProject());
        model.addAttribute("currentEmployeeId", employeeId);
        model.addAttribute("currentProjectId", projectId);
        return "createsubproject";
    }

    // TODO: Vurder om denne kan slettes (kan nok godt)
    @PostMapping("/create/{employeeId}")
    public String createProject(@PathVariable int employeeId,
                                @ModelAttribute Project project,
                                Model model) {
        project.recalculateDuration();

        projectService.createProject(
                project.getProjectName(),
                project.getProjectDescription(),
                project.getProjectStartDate(),
                project.getProjectDeadline(),
                project.getProjectCustomer(),
                employeeId
        );

        return "redirect:/project/list/" + employeeId;
    }

    @PostMapping("/saveproject/{employeeId}")
    public String saveProject(@PathVariable int employeeId,
                              @ModelAttribute Project project) {
        project.recalculateDuration();
        projectService.saveProject(project, employeeId);
//        projectService.assignEmployeeToProject(selectedEmployeeId, project.getProjectID());
        return "redirect:/project/list/" + employeeId;
    }


    @PostMapping("/savesubproject/{employeeId}/{projectId}")
    public String saveSubProject(@PathVariable int employeeId,
                                 @PathVariable long projectId,
                                 @ModelAttribute SubProject subProject) {
        subProject.recalculateDuration();
        projectService.saveSubProject(subProject, projectId);
        return "redirect:/project/subproject/list/" + projectId + "?employeeId=" + employeeId;
    }

    @PostMapping("/delete/{employeeId}/{id}")
    public String deleteProject(@PathVariable int employeeId, @PathVariable long id) {
        projectService.deleteProject(id);
        return "redirect:/project/list/" + employeeId;
    }

    // TODO DELETE TIL SUBPROJECT - kig på den Aden har lavet den
    // TODO: KIG OGSÅ PÅ LINJE 123 EFTER PROJECTID
    @PostMapping("/subproject/delete/{employeeId}/{projectId}/{subProjectId}")
    public String deleteSubProject(@PathVariable int employeeId,
                                   @PathVariable long projectId,
                                   @PathVariable long subProjectId) {
        projectService.deleteSubProject(subProjectId);
        return "redirect:/project/subproject/list/" + projectId + "?employeeId=" + employeeId;
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

    @GetMapping("/subproject/edit/{employeeId}/{projectId}/{subProjectId}")
    public String showSubProjectEditForm(@PathVariable int employeeId,
                                         @PathVariable long projectId,
                                         @PathVariable long subProjectId, // lowercase i URL
                                         Model model) {
        SubProject subProject = projectService.getSubProjectBySubProjectID(subProjectId);
        model.addAttribute("subProject", subProject);
        model.addAttribute("currentEmployeeId", employeeId);
        model.addAttribute("currentProjectId", projectId);

        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee != null) {
            model.addAttribute("username", employee.getUsername());
            model.addAttribute("employeeRole", employee.getRole());
        }

        return "edit-subproject";
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

    @PostMapping("/subproject/edit/{employeeId}/{projectId}/{subProjectId}")
    public String editSubProject(@PathVariable int employeeId,
                                 @PathVariable long projectId,
                                 @PathVariable long subProjectId, // lowercase i URL
                                 @ModelAttribute SubProject subProject) {
        subProject.setSubProjectID(subProjectId); // Bruger setter-metoden fra model klassen
        subProject.recalculateDuration();
        projectService.editSubProject(subProject);
        return "redirect:/project/subproject/list/" + projectId + "?employeeId=" + employeeId;
    }


//    @GetMapping("/assign/{employeeId}/{projectId}")
//    public String showAssignMemberForm(@PathVariable int employeeId,
//                                       @PathVariable long projectId,
//                                       Model model) {
//
//        Project project = projectService.getProjectById(projectId);
//        List<Employee> teamMembers = projectService.getAllTeamMembers();
//
//        model.addAttribute("projectId", projectId);
//        model.addAttribute("teamMembers", teamMembers);
//        model.addAttribute("currentEmployeeId", employeeId);
//
//        return "assign-member";
//    }
//
//
//    @PostMapping("/assign/{employeeId}/{projectId}")
//    public String assignMember(@PathVariable int employeeId,
//                               @PathVariable long projectId,
//                               @RequestParam int selectedEmployeeId) {
//
//        projectService.assignEmployeeToProject(selectedEmployeeId, projectId);
//
//        return "redirect:/project/edit/" + employeeId + "/" + projectId;
//    }

}