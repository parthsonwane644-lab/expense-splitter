package com.parth.ExpenseSplitter.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.*;
import lombok.RequiredArgsConstructor;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepo userRepo;

    public record createUserReq(@NotBlank String name, @Email String email) {
    }

    @PostMapping
    public user CreateUser(@Valid @RequestBody createUserReq req) {
        if (userRepo.existsByEmail(req.email())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        user u = new user();
        u.setEmail(req.email());
        u.setName(req.name());
        return userRepo.save(u);

    }

    @GetMapping
    public List<user> findAll() {
        return userRepo.findAll();
    }

}
