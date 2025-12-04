package com.example.pkveksamen.service;

import com.example.pkveksamen.model.Priority;
import com.example.pkveksamen.model.Status;
import com.example.pkveksamen.model.Task;
import com.example.pkveksamen.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    private TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void createTask(Integer employeeId, long subProjectId, String taskName, String taskDescription
            , Status status, LocalDate startDate, LocalDate endDate, int taskDuration, Priority priority, String taskNote) {
        taskRepository.createTask(employeeId, subProjectId, taskName, taskDescription, status,
                startDate, endDate, taskDuration, priority, taskNote);
    }

    public List<Task> showTaskByEmployeeId(int employeeId) {
        return taskRepository.showTaskByEmployeeId(employeeId);
    }

    public void saveTask(Task task, long subprojectID) {
        taskRepository.saveTask(task, subprojectID);
    }


    public void deleteTask(long taskId) {
        taskRepository.deleteTask(taskId);
    }
    public void createSubTask(Integer employeeId, long projectId, long subProjectId, long taskId, String subTaskName, String subTaskDescription, String subTaskDuration) {
        taskRepository.createSubTask(employeeId, projectId, subProjectId, taskId, subTaskName, subTaskDescription, subTaskDuration);
    }
}




//    public List<Task> getTasksByTaskID(int taskID) {
//        return taskRepository.getTasksByTaskID(taskID);
//    }
//}
