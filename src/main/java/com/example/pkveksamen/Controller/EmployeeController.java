package com.example.pkveksamen.Controller;

import com.example.pkveksamen.Model.Employee;
import com.example.pkveksamen.Service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/")
    public String homepage() {
        return "homepage";
    }

    @GetMapping("login")
    public String getLogin() {
        return "login";
    }

    @GetMapping("/create-employee")
    public String createEmployee(Model model) {
        model.addAttribute("employee", new Employee());
        return "create-employee";
    }

    @PostMapping("/create-employee")
    public String createEmployeePost(Employee employee, Model model) {
        boolean success = employeeService.createEmployee(
                employee.getUsername(),
                employee.getPassword(),
                employee.getEmail(),
                employee.getRole()
        );

        if (!success) {
            model.addAttribute("error", "Denne email er allerede i brug");
            model.addAttribute("employee", employee);
            return "create-employee";
        }

        return "redirect:/login";
    }

    @PostMapping("/validate-login")
    public String validateLogin(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                Model model) {
        Integer id = employeeService.validateLogin(username, password);

        if (id != null && id > 0) {
            return "redirect:/project/list/" + id;
        } else {
            model.addAttribute("error", "Brugernavn eller adgangskoden er forkert. Prøv igen!");
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
