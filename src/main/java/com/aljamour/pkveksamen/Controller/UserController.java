package com.aljamour.pkveksamen.Controller;

import com.aljamour.pkveksamen.Model.UserModel;
import com.aljamour.pkveksamen.Service.UserService;
import org.apache.catalina.User;
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

    @GetMapping
    public String GetLogin(){
        return "/";
    }

    @GetMapping
    public String createUser(Model model){
        model.addAttribute("user", new UserModel());
        return "";
    }

    @PostMapping
    public String

}
