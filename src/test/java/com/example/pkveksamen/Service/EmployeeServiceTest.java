package com.example.pkveksamen.Service;

import com.example.pkveksamen.repository.EmployeeRepository;
import com.example.pkveksamen.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    @Test
    void createEmployee_returnsTrue_whenSuccess() {
        EmployeeRepository repo = mock(EmployeeRepository.class);
        EmployeeService service = new EmployeeService(repo);

        boolean result = service.createEmployee("a", "b", "c", "d", "e");

        assertTrue(result);
        verify(repo).createEmployee("a", "b", "c", "d", "e");
    }

    @Test
    void createEmployee_returnsFalse_whenEmailExists() {
        EmployeeRepository repo = mock(EmployeeRepository.class);
        EmployeeService service = new EmployeeService(repo);

        doThrow(new DataIntegrityViolationException("duplicate"))
                .when(repo).createEmployee(any(), any(), any(), any(), any());

        boolean result = service.createEmployee("a", "b", "c", "d", "e");

        assertFalse(result);
    }
}
