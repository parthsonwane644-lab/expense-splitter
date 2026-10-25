package com.parth.ExpenseSplitter.controller;

import com.parth.ExpenseSplitter.Repo.UserRepo;
import com.parth.ExpenseSplitter.model.user;

import java.util.*;

import lombok.RequiredArgsConstructor;


import org.springframework.web.bind.annotation.*;


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
