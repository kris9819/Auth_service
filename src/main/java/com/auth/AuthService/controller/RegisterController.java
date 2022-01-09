package com.auth.AuthService.controller;

import com.amazonaws.services.memorydb.model.UserAlreadyExistsException;
import com.auth.AuthService.DTO.LoginDTO;
import com.auth.AuthService.DTO.UserDTO;
import com.auth.AuthService.domain.UserData;
import com.auth.AuthService.service.UserService;
import com.auth.AuthService.util.GenericResponse;
import com.auth.AuthService.util.OnRegistrationCompleteEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
public class RegisterController {

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public GenericResponse registerUser(@Valid @RequestBody final UserDTO userDTO,
                                        HttpServletRequest request) {
        try {
            UserData user = userService.registerUser(userDTO);
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(appUrl, request.getLocale(), user));


        } catch (UserAlreadyExistsException exception) {

        }

        return new GenericResponse("Success");
    }

    @GetMapping("registrationConfirm")

    @PostMapping("/login")
    public void login(@RequestBody LoginDTO loginDTO) {
    }

    @GetMapping("/create")
    public void test() {
        userService.test();
    }
}
