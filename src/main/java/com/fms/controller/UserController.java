package com.fms.controller;

import com.fms.entity.User;
import com.fms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String login(User user, HttpSession session){
        User userDB = userService.login(user);
        if(userDB!=null){
            session.setAttribute("user",userDB);
            return "redirect:/file/show";
        }else{
            return "redirect:/index";
        }
    }
}
