package com.parth.ExpenseSplitter;

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
    public ResponseEntity<user> CreateUser(@Valid @RequestBody createUserReq req) {
        if (userRepo.existsByEmail(req.email())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        user u = new user();
        u.setEmail(req.email());
        u.setName(req.name());
        userRepo.save(u);
        return ResponseEntity.ok(u);
    }
    @GetMapping("/{id}")
    public ResponseEntity<user> GetUser(@PathVariable("id") Integer id) {
        user u = userRepo.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return ResponseEntity.ok(u);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> DeleteUser(@PathVariable Integer id) {

        user u = userRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        userRepo.delete(u);
        return ResponseEntity.ok("User deleted");
    }
    @GetMapping
    public ResponseEntity<List<user>> findAll() {
        return ResponseEntity.ok(userRepo.findAll());
    }

}
