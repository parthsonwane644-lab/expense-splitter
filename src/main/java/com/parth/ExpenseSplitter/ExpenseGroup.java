package com.parth.ExpenseSplitter;

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
    private int id;
    private String name;

    @ManyToMany
    private List<user> members=new ArrayList<>();

}

