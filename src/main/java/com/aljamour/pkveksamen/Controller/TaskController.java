package com.aljamour.pkveksamen.Controller;

import com.aljamour.pkveksamen.Model.Task;
import com.aljamour.pkveksamen.Service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class TaskController {

//    private final TaskService taskService;
//
//    public TaskController (TaskService taskService) {
//        this.taskService = taskService;
//    }
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
