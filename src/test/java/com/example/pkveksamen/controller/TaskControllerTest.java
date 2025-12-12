package com.example.pkveksamen.controller;

import com.example.pkveksamen.model.*;
import com.example.pkveksamen.repository.TaskRepository;
import com.example.pkveksamen.service.EmployeeService;
import com.example.pkveksamen.service.ProjectService;
import com.example.pkveksamen.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private ProjectService projectService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private Model model;

    @InjectMocks
    private TaskController taskController;

    private Employee projectManager;
    private Employee teamMember;
    private Task testTask;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        projectManager = new Employee();
        projectManager.setEmployeeId(1);
        projectManager.setUsername("projektleder");
        projectManager.setRole(EmployeeRole.PROJECT_MANAGER);

        teamMember = new Employee();
        teamMember.setEmployeeId(2);
        teamMember.setUsername("udvikler");
        teamMember.setRole(EmployeeRole.TEAM_MEMBER);

        testTask = new Task();
        testTask.setTaskID(1);
        testTask.setTaskName("Test Task");
        testTask.setTaskDescription("Test beskrivelse");
        testTask.setTaskStartDate(LocalDate.of(2025, 1, 1));
        testTask.setTaskDeadline(LocalDate.of(2025, 1, 31));
        testTask.setTaskStatus(Status.NOT_STARTED);
        testTask.setTaskPriority(Priority.MEDIUM);
    }

    @Test
    void isManager_WithProjectManager_ShouldReturnTrue() {
        // Act
        boolean result = taskController.isManager(projectManager);

        // Assert
        assertTrue(result);
    }

    @Test
    void isManager_WithTeamMember_ShouldReturnFalse() {
        // Act
        boolean result = taskController.isManager(teamMember);

        // Assert
        assertFalse(result);
    }


    @Test
    void showTaskByEmployeeId_AsManager_ShouldShowAllTasksInSubProject() {
        // Arrange
        List<Task> tasks = new ArrayList<>();
        tasks.add(testTask);

        when(employeeService.getEmployeeById(1)).thenReturn(projectManager);
        when(taskService.showTasksBySubProjectId(1L)).thenReturn(tasks);

        // Act
        String viewName = taskController.showTaskByEmployeeId(1, 1L, 1L, model);

        // Assert
        assertEquals("task", viewName);
        verify(taskService).showTasksBySubProjectId(1L);
        verify(model).addAttribute("taskList", tasks);
    }

    @Test
    void showTaskByEmployeeId_AsTeamMember_ShouldShowOnlyAssignedTasks() {
        // Arrange
        List<Task> tasks = new ArrayList<>();
        tasks.add(testTask);

        when(employeeService.getEmployeeById(2)).thenReturn(teamMember);
        when(taskService.showTaskByEmployeeId(2)).thenReturn(tasks);

        // Act
        String viewName = taskController.showTaskByEmployeeId(2, 1L, 1L, model);

        // Assert
        assertEquals("task", viewName);
        verify(taskService).showTaskByEmployeeId(2);
        verify(model).addAttribute("taskList", tasks);
    }


    @Test
    void showTaskCreateForm_AsTeamMember_ShouldRedirect() {
        // Arrange
        when(employeeService.getEmployeeById(2)).thenReturn(teamMember);

        // Act
        String viewName = taskController.showTaskCreateForm(2, 1L, 1L, model);

        // Assert
        assertEquals("redirect:/project/task/liste/1/1/2", viewName);
        verify(projectService, never()).getProjectMembers(anyLong());
    }


    @Test
    void deleteTask_ShouldRedirectToTaskList() {
        // Act
        String viewName = taskController.deleteTask(1, 1L, 1L, 1L);

        // Assert
        assertEquals("redirect:/project/task/liste/1/1/1", viewName);
        verify(taskService).deleteTask(1L);
    }


}