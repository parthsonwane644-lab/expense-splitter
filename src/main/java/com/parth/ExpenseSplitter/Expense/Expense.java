package com.parth.ExpenseSplitter.Expense;

import com.parth.ExpenseSplitter.Group.ExpenseGroup;
import com.parth.ExpenseSplitter.user.user;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter @Setter
public class Expense {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String description;
    @Column(precision = 12, scale = 2)
    private BigDecimal amount;

    private LocalDate date=LocalDate.now();

    @ManyToOne(fetch = FetchType.EAGER)
    private user paidBy;

    @ManyToOne(fetch = FetchType.EAGER)
    private ExpenseGroup expenseGroup;

    @ManyToMany
    List<user> participants=new ArrayList<>();
}
