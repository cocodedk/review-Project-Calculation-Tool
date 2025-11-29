package com.example.pkveksamen.Repository;

import com.example.pkveksamen.Model.Employee;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeRepository {

    private final JdbcTemplate jdbcTemplate;

    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createEmployee(String username, String password, String email, String role) {
        jdbcTemplate.update(
                "INSERT INTO employee(username, password, email, role) VALUES (?, ?, ?, ?)",
                username, password, email, role
        );
    }

    public Employee findEmployeeById(int employeeId) {
        String sql = "SELECT employee_id, username, password, email, role FROM employee WHERE employee_id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Employee employee = new Employee();
                employee.setEmployeeId(rs.getInt("employee_id"));
                employee.setUsername(rs.getString("username"));
                employee.setPassword(rs.getString("password"));
                employee.setEmail(rs.getString("email"));
                employee.setRole(rs.getString("role"));
                return employee;
            }, employeeId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Integer validateLogin(String username, String password) {
        try {
            String sql = "SELECT employee_id FROM employee WHERE username = ? AND password = ?";

            List<Integer> result = jdbcTemplate.query(sql, (rs, rowNum) ->
                    rs.getInt("employee_id"),
                    username,
                    password);
            if (!result.isEmpty())
                return result.get(0);
            else
                return 0;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
