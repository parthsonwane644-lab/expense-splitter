package com.parth.ExpenseSplitter.Repo;

import com.parth.ExpenseSplitter.model.user;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<user,Long> {
    boolean existsByEmail(String email);
    Optional<user> findByEmail(String email);

}

