package com.aljamour.pkveksamen.Controller;

import com.aljamour.pkveksamen.Model.UserModel;
import com.aljamour.pkveksamen.Service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService  = userService;
}

    @GetMapping("/")
    public String homepage() {
        return "homepage";
    }

    @GetMapping("/create-user")
    public String createUser(Model model){
        model.addAttribute("user", new UserModel());
        return "create-user";
    }

    // TODO man kan bruge sammen email, vi skal kigge på det
    @PostMapping("/create-user")
    public String createUserpost(UserModel user, Model model) {
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

        return "redirect:/homepage";
    }


}
