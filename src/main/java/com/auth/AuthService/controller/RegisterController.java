package com.auth.AuthService.controller;

import com.auth.AuthService.DTO.LoginDTO;
import com.auth.AuthService.DTO.UserDTO;
import com.auth.AuthService.service.UserService;
import com.auth.AuthService.util.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class RegisterController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public GenericResponse registerUser(@Valid @RequestBody final UserDTO userDTO) {
        userService.registerUser(userDTO);
        return new GenericResponse("Success");
    }

    @PostMapping("/login")
    public void login(@RequestBody LoginDTO loginDTO) {
    }

    @GetMapping("/create")
    public void test() {
        userService.test();
    }
}
