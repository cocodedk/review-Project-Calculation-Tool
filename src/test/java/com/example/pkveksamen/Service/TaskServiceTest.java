package com.example.pkveksamen.Service;

import com.example.pkveksamen.model.Status;
import com.example.pkveksamen.repository.EmployeeRepository;
import com.example.pkveksamen.repository.TaskRepository;
import com.example.pkveksamen.service.TaskService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Test
    void deleteTask_callsRepository() {
        TaskRepository taskRepo = mock(TaskRepository.class);
        EmployeeRepository empRepo = mock(EmployeeRepository.class);
        TaskService service = new TaskService(taskRepo, empRepo);

        service.deleteTask(10L);

        verify(taskRepo).deleteTask(10L);
    }

    @Test
    void updateTaskStatus_callsRepository() {
        TaskRepository taskRepo = mock(TaskRepository.class);
        EmployeeRepository empRepo = mock(EmployeeRepository.class);
        TaskService service = new TaskService(taskRepo, empRepo);

        service.updateTaskStatus(5L, Status.IN_PROGRESS);

        verify(taskRepo).updateTaskStatus(5L, Status.IN_PROGRESS.getDisplayName());

    }
}
