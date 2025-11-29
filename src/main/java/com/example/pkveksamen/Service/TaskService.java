package com.aljamour.pkveksamen.Service;

import com.aljamour.pkveksamen.Model.Task;
import com.aljamour.pkveksamen.Model.TaskPriority;
import com.aljamour.pkveksamen.Model.TaskStatus;
import com.aljamour.pkveksamen.Repository.ProjectRepository;
import com.aljamour.pkveksamen.Repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    private TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void createTask(Integer employeeId, Integer subProjectId, String taskName, String taskDescription
            , TaskStatus taskStatus, LocalDate startDate, LocalDate endDate, int taskDuration, TaskPriority taskPriority, String taskNote) {
        taskRepository.createTask( employeeId, subProjectId, taskName, taskDescription, taskStatus,
                startDate, endDate, taskDuration, taskPriority, taskNote);
    }
}


//    public List<Task> getTasksByTaskID(int taskID) {
//        return taskRepository.getTasksByTaskID(taskID);
//    }
//}
