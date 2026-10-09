package com.parth.ExpenseSplitter.Repo;

import com.parth.ExpenseSplitter.model.ExpenseGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepo extends JpaRepository<ExpenseGroup,Long> {

}
