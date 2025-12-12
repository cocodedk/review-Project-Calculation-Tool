package com.example.pkveksamen.Service;

import com.example.pkveksamen.repository.ProjectRepository;
import com.example.pkveksamen.service.ProjectService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

class ProjectServiceTest {

    @Test
    void createProject_callsRepository() {
        ProjectRepository repo = mock(ProjectRepository.class);
        ProjectService service = new ProjectService(repo);

        service.createProject("t", "d",
                LocalDate.now(), LocalDate.now().plusDays(1),
                "c", 1);

        verify(repo).createProject(any(), any(), any(), any(), any(), any());
    }

    @Test
    void deleteProject_callsRepository() {
        ProjectRepository repo = mock(ProjectRepository.class);
        ProjectService service = new ProjectService(repo);

        service.deleteProject(5L);

        verify(repo).deleteProject(5L);
    }
}
