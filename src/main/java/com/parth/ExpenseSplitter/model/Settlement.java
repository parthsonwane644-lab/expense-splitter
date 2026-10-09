package com.parth.ExpenseSplitter.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Settlement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private ExpenseGroup expenseGroup;
    @ManyToOne
    private user FromUser;
    @ManyToOne
    private user ToUser;

    @Column(precision = 12, scale = 2)
    private BigDecimal amount;
    private LocalDate SettledOn = LocalDate.now();
}

