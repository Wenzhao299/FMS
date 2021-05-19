package com.frms.controller;

import com.frms.entity.User;
import com.frms.service.UserService;
import com.frms.utils.ShaUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Locale;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String login(User user, HttpSession session){
        User userDB = userService.login(user);
        String modify = user.getUsername() + userDB.getSalt() + (userDB.getCounter()+1) + ShaUtil.getSHA256(user.getPwdHash().toUpperCase(Locale.ROOT) + userDB.getSalt() + (userDB.getCounter()+1));
        String modify_db = userDB.getUsername() + userDB.getSalt() + (userDB.getCounter()+1) + ShaUtil.getSHA256(userDB.getPwdHash() + userDB.getSalt() + (userDB.getCounter()+1));
        if(modify.equals(modify_db)) {
            session.setAttribute("user",userDB);
            user.setCounter(userDB.getCounter()+1);
            userService.updateCounter(user.getUsername(),user.getCounter());
            return "redirect:/file/fileList";
        }else{
            return "redirect:/toLoginFailed";
        }
    }

    @PostMapping("/register")
    public String register(User user){
        User userDB = userService.findByUsername(user.getUsername());
        if(userDB==null){
            user.setPwdHash(user.getPwdHash().toUpperCase(Locale.ROOT));
            user.setSalt(RandomStringUtils.randomAlphanumeric(20));
            user.setCounter(0);
            userService.register(user);
            return "redirect:/toLogin";
        }else{
            return "redirect:/toRegisterFailed";
        }
    }
}
