package com.parth.ExpenseSplitter.controller;

import com.parth.ExpenseSplitter.Repo.UserRepo;
import com.parth.ExpenseSplitter.model.user;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.*;
import lombok.RequiredArgsConstructor;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepo userRepo;

    @GetMapping
    public List<user> findAll() {
        return userRepo.findAll();
    }

}
