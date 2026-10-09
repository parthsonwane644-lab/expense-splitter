package com.parth.ExpenseSplitter.Repo;

import com.parth.ExpenseSplitter.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ExpenseRepo extends JpaRepository<Expense, Long> {

    // ExpenseRepo
    List<Expense> findAllByExpenseGroup_Id(Long expenseGroupId);
}
