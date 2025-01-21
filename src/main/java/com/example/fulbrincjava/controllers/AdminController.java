package com.example.fulbrincjava.controllers;

import com.example.fulbrincjava.dtos.UserDto;
import com.example.fulbrincjava.entities.User;
import com.example.fulbrincjava.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admins")
@RestController
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createAdministrator(@RequestBody UserDto userDto) {
        User createdAdmin = userService.createAdministrator(userDto);

        return ResponseEntity.ok(createdAdmin);
    }
}