package com.parth.ExpenseSplitter.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.*;

@Entity
@NoArgsConstructor
@Getter @Setter
public class ExpenseGroup {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    @ManyToMany
    private List<user> members=new ArrayList<>();

}

