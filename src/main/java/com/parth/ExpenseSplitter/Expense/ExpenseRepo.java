package com.parth.ExpenseSplitter.Expense;

import com.parth.ExpenseSplitter.Group.ExpenseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ExpenseRepo extends JpaRepository<Expense, Long> {

    // ExpenseRepo
    List<Expense> findAllByExpenseGroup_Id(Long expenseGroupId);
}
