
package com.example.pkveksamen.service;

import com.example.pkveksamen.model.Priority;
import com.example.pkveksamen.model.Status;
import com.example.pkveksamen.model.SubTask;
import com.example.pkveksamen.model.Task;
import com.example.pkveksamen.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    private static TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void createTask(Integer employeeId, long subProjectId, String taskName, String taskDescription
            , Status status, LocalDate startDate, LocalDate endDate, int taskDuration, Priority priority, String taskNote) {
        taskRepository.createTask( employeeId, subProjectId, taskName, taskDescription, status,
                startDate, endDate, taskDuration, priority, taskNote);
    }

    public List<Task> showTaskByEmployeeId(int employeeId) {
        return taskRepository.showTaskByEmployeeId(employeeId);
    }

    public void saveTask(Task task, int employeeId, long projectId, long subProjectId) {
        task.setTaskDuration(task.getTaskDuration()); // bare for sikkerhed
        task.recalculateDuration();
        taskRepository.saveTask(task, employeeId, projectId, subProjectId);
    }


    public void deleteTask(long taskId) {
        taskRepository.deleteTask(taskId);
    }

    public void editTask(Task task) {
        taskRepository.editTask(task);
    }

    public Task getTaskById(long taskId) {
        return taskRepository.getTaskById(taskId);

    }

    public void createSubTask(long taskId, String subTaskName, String subTaskDescription,
                              String subTaskStatus, LocalDate subTaskStartDate, LocalDate subTaskEndDate,
                              int subTaskDuration, String subTaskPriority, String subTaskNote) {
        taskRepository.createSubTask(taskId, subTaskName, subTaskDescription, subTaskStatus,
                subTaskStartDate, subTaskEndDate, subTaskDuration, subTaskPriority, subTaskNote);
    }

    public void saveSubTask(SubTask subTask, long subTaskId) {
        taskRepository.saveSubTask(subTask, subTaskId);
    }

    public List<SubTask> showSubTasksByTaskId(long taskId) {
        return taskRepository.showSubTasksByTaskId(taskId);
    }

    public void deleteSubTask(long subTaskId) {
        taskRepository.deleteSubTask(subTaskId);
    }

    public void updateTaskNote(long taskId, String taskNote) {
        taskRepository.updateTaskNote(taskId, taskNote);
    }

    public void editSubTask(SubTask subTask) {
        taskRepository.editSubTask(subTask);
    }

    public SubTask getSubTaskById(long subTaskId) {
        return taskRepository.getSubTaskById(subTaskId);
    }
}
