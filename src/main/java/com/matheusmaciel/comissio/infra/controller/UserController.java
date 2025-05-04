package com.matheusmaciel.comissio.infra.controller;

import com.matheusmaciel.comissio.core.domain.model.access.User;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @PostMapping("/")
    public void create(@Valid @RequestBody User user) {
        System.out.println("Creating user: " + user.getEmail());
    }


}
