package com.example.pkveksamen.controller;

import com.example.pkveksamen.model.Employee;
import com.example.pkveksamen.model.SubTask;
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

    public TaskController(TaskService taskService, EmployeeService employeeService, ProjectService projectService) {
        this.taskService = taskService;
        this.employeeService = employeeService;
        this.projectService = projectService;
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

    @GetMapping("/project/task/createtask/{employeeId}/{projectId}/{subProjectId}")
    public String showTaskCreateForm(@PathVariable int employeeId,
                                     @PathVariable long projectId,
                                     @PathVariable long subProjectId,
                                     Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("currentEmployeeId", employeeId);
        model.addAttribute("currentProjectId", projectId);
        model.addAttribute("currentSubProjectId", subProjectId);

        // Tilføj employee info til header
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee != null) {
            model.addAttribute("username", employee.getUsername());
            model.addAttribute("employeeRole", employee.getRole());
        }

        return "createtask";
    }

    @PostMapping("/project/task/createtask/{employeeId}/{projectId}/{subProjectId}")
    public String createTask(@PathVariable int employeeId,
                             @PathVariable long projectId,
                             @PathVariable long subProjectId,
                             @ModelAttribute Task task,
                             Model model) {
        // Calculate duration in days
        if (task.getStartDate() != null && task.getEndDate() != null) {
            long days = ChronoUnit.DAYS.between(task.getStartDate(), task.getEndDate());
            task.setTaskDuration((int) days + 1);
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


    @PostMapping("/task/delete/{employeeId}/{projectId}/{subProjectId}/{taskId}")
    public String deleteTask(@PathVariable int employeeId,
                             @PathVariable long projectId,
                             @PathVariable long subProjectId,
                             @PathVariable long taskId) {
        taskService.deleteTask(taskId);
        return "redirect:/project/subproject/task/list/" + projectId + "/" + subProjectId + "/" + employeeId;
    }
    @GetMapping("/project/subtask/createsubtask/{employeeId}/{projectId}/{subProjectId}/{taskId}")
    public String showSubTaskCreateForm(@PathVariable int employeeId,
                                        @PathVariable long projectId,
                                        @PathVariable long subProjectId,
                                        @PathVariable long taskId,
                                        Model model) {
        model.addAttribute("subTask", new SubTask());
        model.addAttribute("currentEmployeeId", employeeId);
        model.addAttribute("currentProjectId", projectId);
        model.addAttribute("currentSubProjectId", subProjectId);
        model.addAttribute("currentTaskId", taskId);

        // Tilføj employee info til header
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee != null) {
            model.addAttribute("username", employee.getUsername());
            model.addAttribute("employeeRole", employee.getRole());
        }

        return "subtask";
    }


    @PostMapping("/task/subtask/create/{taskId}/{employeeId}/{projectId}/{subProjectId}")
    public String createSubTask(@PathVariable int employeeId,
                                @PathVariable long projectId,
                                @PathVariable long subProjectId,
                                @PathVariable long taskId,
                                @ModelAttribute SubTask subTask) {

        taskService.createSubTask(
                employeeId,
                projectId,
                subProjectId,
                taskId,
                subTask.getSubTaskName(),
                subTask.getSubTaskDescription(),
                subTask.getSubTaskDuration()
        );

        return "redirect:/task/subtask/create/" + taskId + "/" + employeeId + "/" + projectId + "/" + subProjectId;
    }
}


