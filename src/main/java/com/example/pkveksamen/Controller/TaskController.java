package com.aljamour.pkveksamen.Controller;

import com.aljamour.pkveksamen.Model.Task;
import com.aljamour.pkveksamen.Service.EmployeeService;
import com.aljamour.pkveksamen.Service.ProjectService;
import com.aljamour.pkveksamen.Service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("task")
public class TaskController {

    private final TaskService taskService;
    private final EmployeeService employeeService;
    private final ProjectService projectService;

    public TaskController (TaskService taskService, EmployeeService employeeService,ProjectService projectService ) {
        this.taskService = taskService;
        this.employeeService = employeeService;
        this.projectService = projectService;
  }

    @GetMapping("/createtask/{employeeId}")
    public String showTaskCreateform(@PathVariable int employeeId, Model model){
        model.addAttribute("task", new Task());
        model.addAttribute("currentEmployeeId", employeeId);
        return "createtask";
    }



//    @PostMapping("/task/{employeeId}")
//    public String createTask(@PathVariable int employeeId,
//                                @ModelAttribute Task task,
//                                Model model) {
//        task.recalculateDuration();
//
//        task.crea(
//                task.getTaskName(),
//                task.getTaskDescription(),
//                task.getTaskStatus(),
//                task.getStartDate(),
//                task.getEndDate(),
//                task.getTaskDuration(),
//                employeeId
//        );
//
//        return "redirect:/project/list/" + employeeId;
//    }
//}



//    public String showTaskFromProject(@PathVariable int taskID, Model model) {
//        List<Task> tasks = taskService.getTasksByTaskID(taskID);
//        model.addAttribute("tasks", tasks);
//        model.addAttribute("taskID", taskID);
//
//        return "project";
//
//
//    }
}
