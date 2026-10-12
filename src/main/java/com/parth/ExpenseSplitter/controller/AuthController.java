package com.parth.ExpenseSplitter.controller;

import com.parth.ExpenseSplitter.Repo.UserRepo;

import com.parth.ExpenseSplitter.model.user;
import com.parth.ExpenseSplitter.service.jwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepo userRepo;
    private final jwtService jwtService ;
    private final PasswordEncoder passwordEncoder ;

    public record RegisterRequest(@NotBlank String name,@Email String email, @Size(min = 6) String password) {
    }
    public record LoginRequest(String email, String password) {}
    public record AuthResponse( String token ,Long id,String name ) {
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        if(userRepo.existsByEmail(req.email())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists ");
        }
        user u=new user();
        u.setEmail(req.email());
        u.setPassword(passwordEncoder.encode(req.password()));
        u.setName(req.name());
        userRepo.save(u);
        return  new AuthResponse(jwtService.Create(u.getEmail()),u.getId(),u.getName());
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        user u = userRepo.findByEmail(req.email()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "wrong email or password"));

        if (!passwordEncoder.matches(req.password(), u.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "wrong email or password");
        }
        return new AuthResponse(jwtService.Create(u.getEmail()),u.getId(),u.getName());
    }
}
