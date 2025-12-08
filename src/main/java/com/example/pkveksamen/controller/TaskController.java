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
        if (task.getTaskStartDate() != null && task.getTaskDeadline() != null) {
            long days = ChronoUnit.DAYS.between(task.getTaskStartDate(), task.getTaskDeadline());
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
        model.addAttribute("task", task);
        model.addAttribute("currentEmployeeId", employeeId);
        model.addAttribute("currentProjectId", projectId);
        model.addAttribute("currentSubProjectId", subProjectId);

        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee != null) {
            model.addAttribute("username", employee.getUsername());
            model.addAttribute("employeeRole", employee.getRole());
        }

        return "edit-task"; // Thymeleaf template
    }

    @PostMapping("/project/task/edit/{employeeId}/{projectId}/{subProjectId}/{taskId}")
    public String editTask(@PathVariable int employeeId,
                           @PathVariable long projectId,
                           @PathVariable long subProjectId,
                           @PathVariable int taskId,
                           @ModelAttribute Task task) {
        task.setTaskID(taskId);
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
                              @ModelAttribute SubTask subTask) {

        if (subTask.getSubTaskStartDate() != null && subTask.getSubTaskDeadline() != null) {
            long days = ChronoUnit.DAYS.between(subTask.getSubTaskStartDate(), subTask.getSubTaskDeadline());
            subTask.setSubTaskDuration((int) days + 1);
        } else {
            subTask.setSubTaskDuration(0);
        }


        subTask.setSubTaskId(subTaskId);
        taskService.editSubTask(subTask);

        return "redirect:/project/subtask/liste/"
                + projectId + "/" + subProjectId + "/" + taskId + "/" + employeeId;
    }





//
//    // TODO: lav postmapping til opdater subtask status og opdater subtask prioritet
//    @PostMapping("/project/subtask/updatestatus/{subTaskId}")
//        public String updateSubTaskStatus(
//                @PathVariable long subTaskId,
//                @RequestParam(required = false) String subTaskPriority,
//                @RequestParam(required = false) String subTaskStatus) {
//
//            // hent subtask
//            SubTask subTask = taskService.getTaskById()
//
//            // opdater kun det der er sendt med
//            if (subTaskPriority != null) {
//                subTask.setSubTaskPriority(SubTaskPriority.valueOf(subTaskPriority));
//            }
//            if (subTaskStatus != null) {
//                subTask.setSubTaskStatus(SubTaskStatus.valueOf(subTaskStatus));
//            }
//
//            subTaskService.save(subTask);
//
//            // redirect tilbage til listen
//            return "redirect:/task/subtask/list/" + subTaskIdetsTaskEllerProjectId;
//        }

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

//    TODO: lav save task status for teammember
//    @PostMapping("/project/task/updatestatus/{taskId}")
//    public String updateTaskStatus(
//            @PathVariable int employeeId,
//            @PathVariable long projectId,
//            @PathVariable long subProjectId,
//            @PathVariable long taskId,
//            @RequestParam("taskStatus") String taskStatus) {
//
//        taskService.updateTaskStatus(taskId, taskStatus);
//
//        // Redirect tilbage til siden med tasks – ret til din egen URL
//        return "redirect:/project/task/liste/" + projectId + "/" + subProjectId + "/" + employeeId;
//
//    }

    /*
    // TODO: lav save task priority for teammember
    @PostMapping("/project/task/status/{employeeId}/{projectId}/{subProjectId}/{taskId}")
    public String saveTaskPriorityForTeamMember(@PathVariable int employeeId,
                                              @PathVariable long projectId,
                                              @PathVariable long subProjectId,
                                              @PathVariable long taskId,
                                              @RequestParam("taskStatus") String taskStatus) {

        Task task = taskService.getTaskById(taskId);
        task.setTaskNote(taskNote);
        taskService.updateTaskNote(taskId, taskNote); // du har allerede en edit-metode

        return "redirect:/project/task/liste/" + projectId + "/" + subProjectId + "/" + employeeId;
    }
    */
}


