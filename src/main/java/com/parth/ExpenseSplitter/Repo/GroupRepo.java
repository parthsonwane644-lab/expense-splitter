package com.parth.ExpenseSplitter.Repo;

import com.parth.ExpenseSplitter.model.ExpenseGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface GroupRepo extends JpaRepository<ExpenseGroup,Long> {
    List<ExpenseGroup> findByMembersId(Long membersId);
}
