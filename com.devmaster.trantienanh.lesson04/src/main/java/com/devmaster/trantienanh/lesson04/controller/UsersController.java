package com.devmaster.trantienanh.lesson04.controller;

import com.devmaster.trantienanh.lesson04.dto.UserDTO;
import com.devmaster.trantienanh.lesson04.entity.User;
import com.devmaster.trantienanh.lesson04.service.UsersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
    @RequestMapping("/users")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @GetMapping("/list")
    public List<User> getAllUsers() {
        return usersService.findAll();
    }

    @PostMapping("/add")
    public ResponseEntity<String> addUser(@Valid @RequestBody UserDTO userDTO) {
        boolean created = usersService.create(userDTO);
        if (created) {
            return ResponseEntity.ok("User created successfully");
        } else {
            return ResponseEntity.status(500).body("Failed to create user");
        }
    }
}
