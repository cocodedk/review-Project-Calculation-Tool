package com.example.pkveksamen.controller;

import com.example.pkveksamen.model.*;
import com.example.pkveksamen.repository.TaskRepository;
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
    private final TaskRepository taskRepository;

    public TaskController(TaskService taskService, EmployeeService employeeService, ProjectService projectService, TaskRepository taskRepository) {
        this.taskService = taskService;
        this.employeeService = employeeService;
        this.projectService = projectService;
        this.taskRepository = taskRepository;
    }

    private void addEmployeeHeader(Model model, int employeeId) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee != null) {
            model.addAttribute("username", employee.getUsername());
            model.addAttribute("employeeRole", employee.getRole());
        }
    }

    // her laver vi metoderene på hvad de forskellig bruger skal kunne.
    public boolean isManager(Employee employee) {
        return employee != null && employee.getRole() == EmployeeRole.PROJECT_MANAGER;
    }

    public boolean isTeamMember(Employee employee) {
        return employee != null && employee.getRole() == EmployeeRole.TEAM_MEMBER;
    }

    @GetMapping("/project/task/liste/{projectId}/{subProjectId}/{employeeId}")
    public String showTaskByEmployeeId(@PathVariable int employeeId,
                                       @PathVariable long projectId,
                                       @PathVariable long subProjectId,
                                       Model model) {
        Employee currentEmployee = employeeService.getEmployeeById(employeeId);
        List<Task> taskList;

        if (isManager(currentEmployee)) {
            taskList = taskService.showTasksBySubProjectId(subProjectId);
        } else {
            taskList = taskService.showTaskByEmployeeId(employeeId);
        }

        model.addAttribute("taskList", taskList);
        model.addAttribute("currentProjectId", projectId);
        model.addAttribute("currentSubProjectId", subProjectId);
        model.addAttribute("currentEmployeeId", employeeId);

        addEmployeeHeader(model, employeeId);

        return "task";
    }

    @GetMapping("/project/task/createtask/{employeeId}/{projectId}/{subProjectId}")
    public String showTaskCreateForm(@PathVariable int employeeId,
                                     @PathVariable long projectId,
                                     @PathVariable long subProjectId,
                                     Model model) {
        Employee currentEmployee = employeeService.getEmployeeById(employeeId);

        if (!isManager(currentEmployee)) {
            return "redirect:/project/task/liste/" + projectId + "/" + subProjectId + "/" + employeeId;
        }

        List<Employee> projectMembers = projectService.getProjectMembers(projectId);
        for (Employee member : projectMembers) {
            member.setAlphaRoles(employeeService.getEmployeeById(member.getEmployeeId()).getAlphaRoles());
        }

        model.addAttribute("task", new Task());
        model.addAttribute("teamMembers", projectMembers);
        model.addAttribute("currentEmployeeId", employeeId);
        model.addAttribute("currentProjectId", projectId);
        model.addAttribute("currentSubProjectId", subProjectId);
        addEmployeeHeader(model, employeeId);

        return "createtask";
    }

    @PostMapping("/project/task/createtask/{employeeId}/{projectId}/{subProjectId}")
    public String createTask(@PathVariable int employeeId,
                             @PathVariable long projectId,
                             @PathVariable long subProjectId,
                             @ModelAttribute Task task,
                             @RequestParam(value = "assignedToEmployeeId", required = false) Integer assignedToEmployeeId,
                             Model model) {
        if (task.getTaskStartDate() != null && task.getTaskDeadline() != null) {
            long days = ChronoUnit.DAYS.between(task.getTaskStartDate(), task.getTaskDeadline());
            task.setTaskDuration((int) days + 1);
        } else {
            task.setTaskDuration(0);
        }

        if (task.getTaskStartDate() != null) {
            int year = task.getTaskStartDate().getYear();
            if (year < 2000 || year > 2100) {
                model.addAttribute("error", "Start date year must be between 2000 and 2100");
                return "createtask";
            }
        }

        if (task.getTaskDeadline() != null) {
            int year = task.getTaskDeadline().getYear();
            if (year < 2000 || year > 2100) {
                model.addAttribute("error", "Deadline year must be between 2000 and 2100");
                return "createtask";
            }
        }

        if (task.getTaskStatus() == null) {
            task.setTaskStatus(Status.NOT_STARTED);
        }
        if (task.getTaskPriority() == null) {
            task.setTaskPriority(Priority.MEDIUM);
        }

        Project project = projectService.getProjectById(projectId);
        SubProject subProject = projectService.getSubProjectBySubProjectID(subProjectId);
        if (project != null && task.getTaskStartDate() != null && project.getProjectStartDate() != null &&
                task.getTaskStartDate().isBefore(project.getProjectStartDate())) {
            model.addAttribute("error", "Task start date must be within project period");
            model.addAttribute("task", task);
            List<Employee> projectMembers = projectService.getProjectMembers(projectId);
            for (Employee member : projectMembers) {
                member.setAlphaRoles(employeeService.getEmployeeById(member.getEmployeeId()).getAlphaRoles());
            }
            model.addAttribute("teamMembers", projectMembers);
            model.addAttribute("currentEmployeeId", employeeId);
            model.addAttribute("currentProjectId", projectId);
            model.addAttribute("currentSubProjectId", subProjectId);
            return "createtask";
        }
        if (project != null && task.getTaskDeadline() != null && project.getProjectDeadline() != null &&
                task.getTaskDeadline().isAfter(project.getProjectDeadline())) {
            model.addAttribute("error", "Task deadline must be within project period");
            model.addAttribute("task", task);
            List<Employee> projectMembers = projectService.getProjectMembers(projectId);
            for (Employee member : projectMembers) {
                member.setAlphaRoles(employeeService.getEmployeeById(member.getEmployeeId()).getAlphaRoles());
            }
            model.addAttribute("teamMembers", projectMembers);
            model.addAttribute("currentEmployeeId", employeeId);
            model.addAttribute("currentProjectId", projectId);
            model.addAttribute("currentSubProjectId", subProjectId);
            return "createtask";
        }
        if (subProject != null && task.getTaskStartDate() != null && subProject.getSubProjectStartDate() != null &&
                task.getTaskStartDate().isBefore(subProject.getSubProjectStartDate())) {
            model.addAttribute("error", "Task start date must be within subproject period (set in the subproject)");
            model.addAttribute("task", task);
            List<Employee> projectMembers = projectService.getProjectMembers(projectId);
            for (Employee member : projectMembers) {
                member.setAlphaRoles(employeeService.getEmployeeById(member.getEmployeeId()).getAlphaRoles());
            }
            model.addAttribute("teamMembers", projectMembers);
            model.addAttribute("currentEmployeeId", employeeId);
            model.addAttribute("currentProjectId", projectId);
            model.addAttribute("currentSubProjectId", subProjectId);
            return "createtask";
        }
        if (subProject != null && task.getTaskDeadline() != null && subProject.getSubProjectDeadline() != null &&
                task.getTaskDeadline().isAfter(subProject.getSubProjectDeadline())) {
            model.addAttribute("error", "Task deadline must be within subproject period (set in the subproject)");
            model.addAttribute("task", task);
            List<Employee> projectMembers = projectService.getProjectMembers(projectId);
            for (Employee member : projectMembers) {
                member.setAlphaRoles(employeeService.getEmployeeById(member.getEmployeeId()).getAlphaRoles());
            }
            model.addAttribute("teamMembers", projectMembers);
            model.addAttribute("currentEmployeeId", employeeId);
            model.addAttribute("currentProjectId", projectId);
            model.addAttribute("currentSubProjectId", subProjectId);
            return "createtask";
        }
        if (task.getTaskStartDate() != null && task.getTaskDeadline() != null &&
                task.getTaskDeadline().isBefore(task.getTaskStartDate())) {
            model.addAttribute("error", "Task deadline cannot be before start date");
            model.addAttribute("task", task);
            List<Employee> projectMembers = projectService.getProjectMembers(projectId);
            for (Employee member : projectMembers) {
                member.setAlphaRoles(employeeService.getEmployeeById(member.getEmployeeId()).getAlphaRoles());
            }
            model.addAttribute("teamMembers", projectMembers);
            model.addAttribute("currentEmployeeId", employeeId);
            model.addAttribute("currentProjectId", projectId);
            model.addAttribute("currentSubProjectId", subProjectId);
            return "createtask";
        }

        Integer employeeIdToAssign = assignedToEmployeeId != null ? assignedToEmployeeId : employeeId;

        taskService.createTask(
                employeeIdToAssign,
                subProjectId,
                task.getTaskName(),
                task.getTaskDescription(),
                task.getTaskStatus(),
                task.getTaskStartDate(),
                task.getTaskDeadline(),
                task.getTaskDuration(),
                task.getTaskPriority(),
                task.getTaskNote()
        );

        return "redirect:/project/task/liste/" + projectId + "/" + subProjectId + "/" + employeeId;
    }


    @PostMapping("/save/{employeeId}/{projectId}/{subProjectId}")
    public String saveTask(@PathVariable int employeeId,
                           @PathVariable long projectId,
                           @PathVariable long subProjectId,
                           @ModelAttribute Task task) {
        taskService.saveTask(task, employeeId, projectId, subProjectId);
        return "redirect:/project/task/liste/" + projectId + "/" + subProjectId + "/" + employeeId;
    }


    @PostMapping("/task/delete/{employeeId}/{projectId}/{subProjectId}/{taskId}")
    public String deleteTask(@PathVariable int employeeId,
                             @PathVariable long projectId,
                             @PathVariable long subProjectId,
                             @PathVariable long taskId) {

        taskService.deleteTask(taskId);

        return "redirect:/project/task/liste/" + projectId + "/" + subProjectId + "/" + employeeId;
    }

    @GetMapping("/project/task/edit/{employeeId}/{projectId}/{subProjectId}/{taskId}")
    public String showEditTaskForm(@PathVariable int employeeId,
                                   @PathVariable long projectId,
                                   @PathVariable long subProjectId,
                                   @PathVariable long taskId,
                                   Model model) {
        Task task = taskService.getTaskById(taskId);
        if (task.getAssignedEmployee() != null) {
            task.getAssignedEmployee().setAlphaRoles(employeeService.getEmployeeById(task.getAssignedEmployee().getEmployeeId()).getAlphaRoles());
        }
        
        List<Employee> projectMembers = projectService.getProjectMembers(projectId);
        for (Employee member : projectMembers) {
            member.setAlphaRoles(employeeService.getEmployeeById(member.getEmployeeId()).getAlphaRoles());
        }
        
        model.addAttribute("task", task);
        model.addAttribute("teamMembers", projectMembers);
        model.addAttribute("currentEmployeeId", employeeId);
        model.addAttribute("currentProjectId", projectId);
        model.addAttribute("currentSubProjectId", subProjectId);

        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee != null) {
            model.addAttribute("username", employee.getUsername());
            model.addAttribute("employeeRole", employee.getRole());
        }

        return "edit-task";
    }

    @PostMapping("/project/task/edit/{employeeId}/{projectId}/{subProjectId}/{taskId}")
    public String editTask(@PathVariable int employeeId,
                           @PathVariable long projectId,
                           @PathVariable long subProjectId,
                           @PathVariable int taskId,
                           @ModelAttribute Task task,
                           @RequestParam(value = "assignedToEmployeeId", required = false) Integer assignedToEmployeeId,
                           Model model) {
        task.setTaskID(taskId);
        if (assignedToEmployeeId != null) {
            Employee assignedEmployee = new Employee();
            assignedEmployee.setEmployeeId(assignedToEmployeeId);
            task.setAssignedEmployee(assignedEmployee);
        }
        if (task.getTaskStartDate() != null && task.getTaskDeadline() != null) {
            long days = ChronoUnit.DAYS.between(task.getTaskStartDate(), task.getTaskDeadline());
            task.setTaskDuration((int) days + 1);
        } else {
            task.setTaskDuration(0);
        }

        Project project = projectService.getProjectById(projectId);
        SubProject subProject = projectService.getSubProjectBySubProjectID(subProjectId);
        if (project != null && task.getTaskStartDate() != null && project.getProjectStartDate() != null &&
                task.getTaskStartDate().isBefore(project.getProjectStartDate())) {
            model.addAttribute("error", "Task start date must be within project period");
            model.addAttribute("task", task);
            List<Employee> projectMembers = projectService.getProjectMembers(projectId);
            for (Employee member : projectMembers) {
                member.setAlphaRoles(employeeService.getEmployeeById(member.getEmployeeId()).getAlphaRoles());
            }
            model.addAttribute("teamMembers", projectMembers);
            model.addAttribute("currentEmployeeId", employeeId);
            model.addAttribute("currentProjectId", projectId);
            model.addAttribute("currentSubProjectId", subProjectId);
            addEmployeeHeader(model, employeeId);
            return "edit-task";
        }
        if (project != null && task.getTaskDeadline() != null && project.getProjectDeadline() != null &&
                task.getTaskDeadline().isAfter(project.getProjectDeadline())) {
            model.addAttribute("error", "Task deadline must be within project period");
            model.addAttribute("task", task);
            List<Employee> projectMembers = projectService.getProjectMembers(projectId);
            for (Employee member : projectMembers) {
                member.setAlphaRoles(employeeService.getEmployeeById(member.getEmployeeId()).getAlphaRoles());
            }
            model.addAttribute("teamMembers", projectMembers);
            model.addAttribute("currentEmployeeId", employeeId);
            model.addAttribute("currentProjectId", projectId);
            model.addAttribute("currentSubProjectId", subProjectId);
            addEmployeeHeader(model, employeeId);
            return "edit-task";
        }
        if (subProject != null && task.getTaskStartDate() != null && subProject.getSubProjectStartDate() != null &&
                task.getTaskStartDate().isBefore(subProject.getSubProjectStartDate())) {
            model.addAttribute("error", "Task start date must be within subproject period (set in the subproject)");
            model.addAttribute("task", task);
            List<Employee> projectMembers = projectService.getProjectMembers(projectId);
            for (Employee member : projectMembers) {
                member.setAlphaRoles(employeeService.getEmployeeById(member.getEmployeeId()).getAlphaRoles());
            }
            model.addAttribute("teamMembers", projectMembers);
            model.addAttribute("currentEmployeeId", employeeId);
            model.addAttribute("currentProjectId", projectId);
            model.addAttribute("currentSubProjectId", subProjectId);
            addEmployeeHeader(model, employeeId);
            return "edit-task";
        }
        if (subProject != null && task.getTaskDeadline() != null && subProject.getSubProjectDeadline() != null &&
                task.getTaskDeadline().isAfter(subProject.getSubProjectDeadline())) {
            model.addAttribute("error", "Task deadline must be within subproject period (set in the subproject)");
            model.addAttribute("task", task);
            List<Employee> projectMembers = projectService.getProjectMembers(projectId);
            for (Employee member : projectMembers) {
                member.setAlphaRoles(employeeService.getEmployeeById(member.getEmployeeId()).getAlphaRoles());
            }
            model.addAttribute("teamMembers", projectMembers);
            model.addAttribute("currentEmployeeId", employeeId);
            model.addAttribute("currentProjectId", projectId);
            model.addAttribute("currentSubProjectId", subProjectId);
            addEmployeeHeader(model, employeeId);
            return "edit-task";
        }
        if (task.getTaskStartDate() != null && task.getTaskDeadline() != null &&
                task.getTaskDeadline().isBefore(task.getTaskStartDate())) {
            model.addAttribute("error", "Task deadline cannot be before start date");
            model.addAttribute("task", task);
            List<Employee> projectMembers = projectService.getProjectMembers(projectId);
            for (Employee member : projectMembers) {
                member.setAlphaRoles(employeeService.getEmployeeById(member.getEmployeeId()).getAlphaRoles());
            }
            model.addAttribute("teamMembers", projectMembers);
            model.addAttribute("currentEmployeeId", employeeId);
            model.addAttribute("currentProjectId", projectId);
            model.addAttribute("currentSubProjectId", subProjectId);
            addEmployeeHeader(model, employeeId);
            return "edit-task";
        }

        taskService.editTask(task);
        return "redirect:/project/task/liste/" + projectId + "/" + subProjectId + "/" + employeeId;
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

        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee != null) {
            model.addAttribute("username", employee.getUsername());
            model.addAttribute("employeeRole", employee.getRole());
        }

        return "createsubtask";
    }

    @GetMapping("/project/subtask/liste/{projectId}/{subProjectId}/{taskId}/{employeeId}")
    public String showSubTasksByTaskId(@PathVariable int employeeId,
                                       @PathVariable long projectId,
                                       @PathVariable long subProjectId,
                                       @PathVariable long taskId,
                                       Model model) {

        // Ret dette: Hent subtasks for den specifikke task, ikke alle employee's subtasks
        List<SubTask> subTaskList = taskService.showSubTasksByTaskId(taskId);

        model.addAttribute("subTaskList", subTaskList);
        model.addAttribute("currentProjectId", projectId);
        model.addAttribute("currentSubProjectId", subProjectId);
        model.addAttribute("currentEmployeeId", employeeId);
        model.addAttribute("currentTaskId", taskId);

        // Add employee details for the header
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee != null) {
            model.addAttribute("username", employee.getUsername());
            model.addAttribute("employeeRole", employee.getRole());
        }

        return "subtask";
    }

    @PostMapping("/project/subtask/createsubtask/{employeeId}/{projectId}/{subProjectId}/{taskId}")
    public String createSubTask(@PathVariable int employeeId,
                                @PathVariable long projectId,
                                @PathVariable long subProjectId,
                                @PathVariable long taskId,
                                @ModelAttribute SubTask subTask,
                                Model model) {

        // TILFØJ DENNE TRY-CATCH
        try {
            Task parentTask = taskService.getTaskById(taskId);
            if (parentTask == null) {
                System.out.println("ERROR - Task findes ikke: taskId=" + taskId);
                return "redirect:/project/task/liste/" + projectId + "/" + subProjectId + "/" + employeeId;
            }
        } catch (Exception e) {
            System.out.println("ERROR - Task findes ikke i databasen: taskId=" + taskId);
            System.out.println("Tjek din task.html - linket sender forkert taskId!");
            return "redirect:/project/task/liste/" + projectId + "/" + subProjectId + "/" + employeeId;
        }

        // Calculate duration in days
        if (subTask.getSubTaskStartDate() != null && subTask.getSubTaskDeadline() != null) {
            long days = ChronoUnit.DAYS.between(subTask.getSubTaskStartDate(), subTask.getSubTaskDeadline());
            subTask.setSubTaskDuration((int) days + 1);
        } else {
            subTask.setSubTaskDuration(0);
        }

        // Simpel range-check
        if (subTask.getSubTaskStartDate() != null) {
            int year = subTask.getSubTaskStartDate().getYear();
            if (year < 2000 || year > 2100) {
                // her kunne du fx sætte en fejlbesked i model og vise formen igen
                model.addAttribute("error", "Start date year must be between 2000 and 2100");
                // husk at lægge de samme model-attributter på som i GET-metoden
                return "createsubtask";
            }
        }

        if (subTask.getSubTaskDeadline() != null) {
            int year = subTask.getSubTaskDeadline().getYear();
            if (year < 2000 || year > 2100) {
                model.addAttribute("error", "Deadline year must be between 2000 and 2100");
                return "createsubtask";
            }
        }

        // default status & priority hvis de er null
        if (subTask.getSubTaskStatus() == null) {
            subTask.setSubTaskStatus(Status.NOT_STARTED);
        }
        if (subTask.getSubTaskPriority() == null) {
            subTask.setSubTaskPriority(Priority.MEDIUM);
        }

        Task parentTask = taskService.getTaskById(taskId);
        if (parentTask != null && parentTask.getTaskStartDate() != null && subTask.getSubTaskStartDate() != null &&
                subTask.getSubTaskStartDate().isBefore(parentTask.getTaskStartDate())) {
            model.addAttribute("error", "Subtask start date must be within task period");
            model.addAttribute("subTask", subTask);
            model.addAttribute("currentEmployeeId", employeeId);
            model.addAttribute("currentProjectId", projectId);
            model.addAttribute("currentSubProjectId", subProjectId);
            model.addAttribute("currentTaskId", taskId);
            Employee employee = employeeService.getEmployeeById(employeeId);
            if (employee != null) {
                model.addAttribute("username", employee.getUsername());
                model.addAttribute("employeeRole", employee.getRole());
            }
            return "createsubtask";
        }
        if (parentTask != null && parentTask.getTaskDeadline() != null && subTask.getSubTaskDeadline() != null &&
                subTask.getSubTaskDeadline().isAfter(parentTask.getTaskDeadline())) {
            model.addAttribute("error", "Subtask deadline must be within task period");
            model.addAttribute("subTask", subTask);
            model.addAttribute("currentEmployeeId", employeeId);
            model.addAttribute("currentProjectId", projectId);
            model.addAttribute("currentSubProjectId", subProjectId);
            model.addAttribute("currentTaskId", taskId);
            Employee employee = employeeService.getEmployeeById(employeeId);
            if (employee != null) {
                model.addAttribute("username", employee.getUsername());
                model.addAttribute("employeeRole", employee.getRole());
            }
            return "createsubtask";
        }
        if (subTask.getSubTaskStartDate() != null && subTask.getSubTaskDeadline() != null &&
                subTask.getSubTaskDeadline().isBefore(subTask.getSubTaskStartDate())) {
            model.addAttribute("error", "Subtask deadline cannot be before start date");
            model.addAttribute("subTask", subTask);
            model.addAttribute("currentEmployeeId", employeeId);
            model.addAttribute("currentProjectId", projectId);
            model.addAttribute("currentSubProjectId", subProjectId);
            model.addAttribute("currentTaskId", taskId);
            Employee employee = employeeService.getEmployeeById(employeeId);
            if (employee != null) {
                model.addAttribute("username", employee.getUsername());
                model.addAttribute("employeeRole", employee.getRole());
            }
            return "createsubtask";
        }

        taskService.createSubTask(
                taskId,
                subTask.getSubTaskName(),
                subTask.getSubTaskDescription(),
                subTask.getSubTaskStatus().name(),
                subTask.getSubTaskStartDate(),
                subTask.getSubTaskDeadline(),
                subTask.getSubTaskDuration(),
                subTask.getSubTaskPriority().name(),
                subTask.getSubTaskNote()
        );

        return "redirect:/project/subtask/liste/" + projectId + "/" + subProjectId + "/" + taskId + "/" + employeeId;
    }

//    @PostMapping("/subtask/save/{employeeId}/{projectId}/{subProjectId}/{taskId}/{subTaskId}")
//    public String saveSubTask(@PathVariable int employeeId,
//                              @PathVariable long projectId,
//                              @PathVariable long subProjectId,
//                              @PathVariable long taskId,
//                              @PathVariable long subTaskId,
//                              @ModelAttribute SubTask subTask) {
//        taskService.saveSubTask(subTask, subTaskId);
//        subTask.recalculateDuration();
//
//
//        return "redirect:/project/task/subtask/liste/" + projectId + "/" + subProjectId + "/" + taskId + "/" + employeeId;
//    }

    @PostMapping("/subtask/delete/{employeeId}/{projectId}/{subProjectId}/{taskId}/{subTaskId}")
    public String deleteSubTask(@PathVariable int employeeId,
                                @PathVariable long projectId,
                                @PathVariable long subProjectId,
                                @PathVariable long taskId,
                                @PathVariable long subTaskId) {
        taskService.deleteSubTask(subTaskId);
        return "redirect:/project/subtask/liste/" + projectId + "/" + subProjectId + "/" + taskId + "/" + employeeId;
    }


    @GetMapping("/project/subtask/edit/{employeeId}/{projectId}/{subProjectId}/{taskId}/{subTaskId}")
    public String showEditSubTaskForm(@PathVariable int employeeId,
                                      @PathVariable long projectId,
                                      @PathVariable long subProjectId,
                                      @PathVariable long taskId,
                                      @PathVariable long subTaskId,
                                      Model model) {

        SubTask subTask = taskService.getSubTaskById(subTaskId);
        model.addAttribute("subTask", subTask);

        model.addAttribute("currentEmployeeId", employeeId);
        model.addAttribute("currentProjectId", projectId);
        model.addAttribute("currentSubProjectId", subProjectId);
        model.addAttribute("currentTaskId", taskId);

        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee != null) {
            model.addAttribute("username", employee.getUsername());
            model.addAttribute("employeeRole", employee.getRole());
        }

        return "edit-subtask"; // Thymeleaf HTML-fil
    }


    @PostMapping("/project/subtask/edit/{employeeId}/{projectId}/{subProjectId}/{taskId}/{subTaskId}")
    public String editSubTask(@PathVariable int employeeId,
                              @PathVariable long projectId,
                              @PathVariable long subProjectId,
                              @PathVariable long taskId,
                              @PathVariable long subTaskId,
                              @ModelAttribute SubTask subTask,
                              Model model) {

        if (subTask.getSubTaskStartDate() != null && subTask.getSubTaskDeadline() != null) {
            long days = ChronoUnit.DAYS.between(subTask.getSubTaskStartDate(), subTask.getSubTaskDeadline());
            subTask.setSubTaskDuration((int) days + 1);
        } else {
            subTask.setSubTaskDuration(0);
        }


        subTask.setSubTaskId(subTaskId);
        Task parentTask = taskService.getTaskById(taskId);
        if (parentTask != null && parentTask.getTaskStartDate() != null && subTask.getSubTaskStartDate() != null &&
                subTask.getSubTaskStartDate().isBefore(parentTask.getTaskStartDate())) {
            model.addAttribute("error", "Subtask start date must be within task period");
            model.addAttribute("subTask", subTask);
            model.addAttribute("currentEmployeeId", employeeId);
            model.addAttribute("currentProjectId", projectId);
            model.addAttribute("currentSubProjectId", subProjectId);
            model.addAttribute("currentTaskId", taskId);
            Employee employee = employeeService.getEmployeeById(employeeId);
            if (employee != null) {
                model.addAttribute("username", employee.getUsername());
                model.addAttribute("employeeRole", employee.getRole());
            }
            return "edit-subtask";
        }
        if (parentTask != null && parentTask.getTaskDeadline() != null && subTask.getSubTaskDeadline() != null &&
                subTask.getSubTaskDeadline().isAfter(parentTask.getTaskDeadline())) {
            model.addAttribute("error", "Subtask deadline must be within task period");
            model.addAttribute("subTask", subTask);
            model.addAttribute("currentEmployeeId", employeeId);
            model.addAttribute("currentProjectId", projectId);
            model.addAttribute("currentSubProjectId", subProjectId);
            model.addAttribute("currentTaskId", taskId);
            Employee employee = employeeService.getEmployeeById(employeeId);
            if (employee != null) {
                model.addAttribute("username", employee.getUsername());
                model.addAttribute("employeeRole", employee.getRole());
            }
            return "edit-subtask";
        }
        if (subTask.getSubTaskStartDate() != null && subTask.getSubTaskDeadline() != null &&
                subTask.getSubTaskDeadline().isBefore(subTask.getSubTaskStartDate())) {
            model.addAttribute("error", "Subtask deadline cannot be before start date");
            model.addAttribute("subTask", subTask);
            model.addAttribute("currentEmployeeId", employeeId);
            model.addAttribute("currentProjectId", projectId);
            model.addAttribute("currentSubProjectId", subProjectId);
            model.addAttribute("currentTaskId", taskId);
            Employee employee = employeeService.getEmployeeById(employeeId);
            if (employee != null) {
                model.addAttribute("username", employee.getUsername());
                model.addAttribute("employeeRole", employee.getRole());
            }
            return "edit-subtask";
        }

        taskService.editSubTask(subTask);

        return "redirect:/project/subtask/liste/"
                + projectId + "/" + subProjectId + "/" + taskId + "/" + employeeId;
    }

    @GetMapping("/project/subtask/note/{employeeId}/{projectId}/{subProjectId}/{subTaskId}")
    public String showSubTaskNoteForm(@PathVariable int employeeId,
                                      @PathVariable long projectId,
                                      @PathVariable long subProjectId,
                                      @PathVariable long subTaskId,
                                      @RequestParam("taskId") long taskId,
                                      Model model) {

        SubTask subTask = taskService.getSubTaskById(subTaskId);

        model.addAttribute("subTask", subTask);
        model.addAttribute("currentEmployeeId", employeeId);
        model.addAttribute("currentProjectId", projectId);
        model.addAttribute("currentSubProjectId", subProjectId);
        model.addAttribute("currentTaskId", taskId);

        addEmployeeHeader(model, employeeId);

        return "subtask-note";
    }

    @PostMapping("/project/subtask/note/{employeeId}/{projectId}/{subProjectId}/{subTaskId}")
    public String saveSubTaskNote(@PathVariable int employeeId,
                                  @PathVariable long projectId,
                                  @PathVariable long subProjectId,
                                  @PathVariable long subTaskId,
                                  @RequestParam("taskId") long taskId,
                                  @RequestParam("subTaskNote") String subTaskNote) {

        taskService.updateSubTaskNote(subTaskId, subTaskNote);

        return "redirect:/project/subtask/liste/" + projectId + "/" + subProjectId + "/" + taskId + "/" + employeeId;
    }

    @GetMapping("/project/task/note/{employeeId}/{projectId}/{subProjectId}/{taskId}")
    public String showTaskNoteForm(@PathVariable int employeeId,
                                   @PathVariable long projectId,
                                   @PathVariable long subProjectId,
                                   @PathVariable long taskId,
                                   Model model) {
        Task task = taskService.getTaskById(taskId);

        model.addAttribute("task", task);
        model.addAttribute("currentEmployeeId", employeeId);
        model.addAttribute("currentProjectId", projectId);
        model.addAttribute("currentSubProjectId", subProjectId);

        // Til header
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee != null) {
            model.addAttribute("username", employee.getUsername());
            model.addAttribute("employeeRole", employee.getRole());
        }

        return "task-note"; // ny Thymeleaf-template
    }

    @PostMapping("/project/task/note/{employeeId}/{projectId}/{subProjectId}/{taskId}")
    public String saveTaskNote(@PathVariable int employeeId,
                               @PathVariable long projectId,
                               @PathVariable long subProjectId,
                               @PathVariable long taskId,
                               @RequestParam("taskNote") String taskNote) {

        Task task = taskService.getTaskById(taskId);
        task.setTaskNote(taskNote);
        taskService.updateTaskNote(taskId, taskNote); // du har allerede en edit-metode

        return "redirect:/project/task/liste/" + projectId + "/" + subProjectId + "/" + employeeId;
    }

    @PostMapping("/project/task/updatestatus/{taskId}")
    public String updateTaskStatus(
            @PathVariable long taskId,
            @RequestParam("taskStatus") String taskStatus,
            @RequestParam("employeeId") int employeeId,
            @RequestParam("projectId") long projectId,
            @RequestParam("subProjectId") long subProjectId) {

        Status status = Status.valueOf(taskStatus);
        taskService.updateTaskStatus(taskId, status);

        return "redirect:/project/task/liste/" + projectId + "/" + subProjectId + "/" + employeeId;
    }

    @PostMapping("/project/task/updatepriority/{taskId}")
    public String updateTaskPriority(
            @PathVariable long taskId,
            @RequestParam("taskPriority") String taskPriority,
            @RequestParam("employeeId") int employeeId,
            @RequestParam("projectId") long projectId,
            @RequestParam("subProjectId") long subProjectId) {

        Priority priority = Priority.valueOf(taskPriority);
        taskService.updateTaskPriority(taskId, priority);

        return "redirect:/project/task/liste/" + projectId + "/" + subProjectId + "/" + employeeId;
    }

    @PostMapping("/project/subtask/updatestatus/{subTaskId}")
    public String updateSubTaskStatus(
            @PathVariable long subTaskId,
            @RequestParam("subTaskStatus") String subTaskStatus,
            @RequestParam("employeeId") int employeeId,
            @RequestParam("projectId") long projectId,
            @RequestParam("subProjectId") long subProjectId,
            @RequestParam("taskId") long taskId) {

        Status status = Status.valueOf(subTaskStatus);
        taskService.updateSubTaskStatus(subTaskId, status);

        return "redirect:/project/subtask/liste/" + projectId + "/" + subProjectId + "/" + taskId + "/" + employeeId;
    }

    @PostMapping("/project/subtask/updatepriority/{subTaskId}")
    public String updateSubTaskPriority(
            @PathVariable long subTaskId,
            @RequestParam("subTaskPriority") String subTaskPriority,
            @RequestParam("employeeId") int employeeId,
            @RequestParam("projectId") long projectId,
            @RequestParam("subProjectId") long subProjectId,
            @RequestParam("taskId") long taskId) {

        Priority priority = Priority.valueOf(subTaskPriority);
        taskService.updateSubTaskPriority(subTaskId, priority);

        return "redirect:/project/subtask/liste/" + projectId + "/" + subProjectId + "/" + taskId + "/" + employeeId;
    }
}


