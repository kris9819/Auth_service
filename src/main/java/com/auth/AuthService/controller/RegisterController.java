package com.auth.AuthService.controller;

import com.auth.AuthService.domain.UserData;
import com.auth.AuthService.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterController {

    @Autowired
    RegisterService registerService;

    @PostMapping("/register")
    public void registerUser(@RequestBody UserData userData) {
        registerService.registerUser(userData);
    }

    @PostMapping("/login")
    public void login(@RequestBody UserData userData) {
        registerService.findUser(userData);
    }

    @GetMapping("/create")
    public void test() {
        registerService.test();
    }
}
