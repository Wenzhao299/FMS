package com.frms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("toLogin")
    public String toLogin(){
        return "login";
    }

    @GetMapping("toLoginFailed")
    public String toLoginFailed(){
        return "loginFailed";
    }

    @GetMapping("toRegister")
    public String toRegister(){
        return "register";
    }

    @GetMapping("toRegisterFailed")
    public String toRegisterFailed(){
        return "registerFailed";
    }

    @GetMapping("toFileList")
    public String toFileList(){
        return "fileList";
    }

    @GetMapping("toUploadFile")
    public String toUploadFile(){
        return "uploadFile";
    }

}
