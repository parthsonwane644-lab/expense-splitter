package com.parth.ExpenseSplitter.component;

import com.parth.ExpenseSplitter.Repo.UserRepo;
import com.parth.ExpenseSplitter.model.user;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUser {
    private final UserRepo userRepo;

    public user get(){
        var auth= SecurityContextHolder.getContext().getAuthentication();
        String email=  (String)auth.getPrincipal();
        return userRepo.findByEmail(email).orElseThrow();
    }
}
