package com.example.pkveksamen.controller;

import com.example.pkveksamen.model.Employee;
import com.example.pkveksamen.model.Task;
import com.example.pkveksamen.service.EmployeeService;
import com.example.pkveksamen.service.ProjectService;
import com.example.pkveksamen.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.temporal.ChronoUnit;

@Controller
public class TaskController {

    private final TaskService taskService;
    private final EmployeeService employeeService;
    private final ProjectService projectService;

    public TaskController (TaskService taskService, EmployeeService employeeService,ProjectService projectService) {
        this.taskService = taskService;
        this.employeeService = employeeService;
        this.projectService = projectService;
    }


    @GetMapping("/createtask/{projectId}/{subProjectId}")
    public String showTaskCreateForm(@PathVariable long subProjectId,
                                     @PathVariable long projectId ,
                                     Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("currentProjectId", projectId);
        model.addAttribute("currentSubProjectId", subProjectId);
        return "createtask";
    }

    @PostMapping("/createtask/{subProjectId}")
    public String createTask(@PathVariable Integer employeeId,
                             @PathVariable long subProjectId,
                             @RequestParam long projectId,
                             @ModelAttribute Task task,
                             Model model) {
        // Calculate duration in days
        if (task.getStartDate() != null && task.getEndDate() != null) {
            long days = ChronoUnit.DAYS.between(task.getStartDate(), task.getEndDate());
            task.setTaskDuration((int) days + 1); // +1 to include both start and end dates
        } else {
            task.setTaskDuration(0);
        }

        taskService.createTask(
                employeeId,
                subProjectId,
                task.getTaskName(),
                task.getTaskDescription(),
                task.getTaskStatus(),
                task.getStartDate(),
                task.getEndDate(),
                task.getTaskDuration(),
                task.getTaskPriority(),
                task.getTaskNote()
        );

        return "redirect:/project/task/liste/" + projectId + "/" + subProjectId + "/" + employeeId;
    }

    @GetMapping("/project/task/liste/{projectId}/{subProjectId}/{employeeId}")
    public String showTaskByEmployeeId(@PathVariable int employeeId,
                                       @PathVariable long projectId,
                                       @PathVariable long subProjectId,
                                       Model model) {
        List<Task> taskList = taskService.showTaskByEmployeeId(employeeId);
        model.addAttribute("taskList", taskList);
        model.addAttribute("currentProjectId", projectId);
        model.addAttribute("currentSubProjectId", subProjectId);
        model.addAttribute("currentEmployeeId", employeeId);

        // Add employee details for the header
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee != null) {
            model.addAttribute("username", employee.getUsername());
            model.addAttribute("employeeRole", employee.getRole());
        }

        return "task";
    }
}