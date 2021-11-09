package com.auth.AuthService.controller;

import com.auth.AuthService.service.RabbitTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

@RestController
public class RabbitTestController {

    @Autowired
    RabbitTestService rabbitTestService;

    @GetMapping("/test")
    public void test() throws NoSuchAlgorithmException, IOException, KeyManagementException, TimeoutException {
        rabbitTestService.sendMsg();
    }
}
