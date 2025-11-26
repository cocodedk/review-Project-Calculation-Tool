package com.aljamour.pkveksamen.Controller;

import com.aljamour.pkveksamen.Model.User;
import com.aljamour.pkveksamen.Service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String homepage() {
        return "homepage";
    }

    @GetMapping("login")
    public String getLogin() {
        return "login";
    }

    @GetMapping("/create-user")
    public String createUser(Model model) {
        model.addAttribute("user", new User());
        return "create-user";
    }

    @PostMapping("/create-user")
    public String createUserpost(User user, Model model) {
        boolean success = userService.createUser(
                user.getUserName(),
                user.getEmail(),
                user.getUserPassword(),
                user.getRole()
        );

        if (!success) {
            model.addAttribute("error", "Denne email er allerede i brug");
            model.addAttribute("user", user);
            return "create-user";
        }

        return "redirect:/login";
    }

    // I UserController.java

    @PostMapping("/validate-login")
    public String validateLogin(@RequestParam("username") String userName,
                                @RequestParam("password") String userPassword, Model model) {
        Integer id = userService.validateLogin(userName, userPassword);

        if (id != null && id > 0) {
            return "redirect:/project/list/" + id;
        } else {
            model.addAttribute("error", "Brugernavn eller kode er forkert. Prøv igen!");

            return "login";
        }
    }
}