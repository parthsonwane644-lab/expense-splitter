package com.parth.ExpenseSplitter;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<user,Integer> {
    boolean existsByEmail(String email);
    Optional<user> findByEmail(String email);

}

