package com.parth.ExpenseSplitter.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="users")
@NoArgsConstructor
@Getter @Setter

public class user {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long  id;
    private String name;
    @Column(unique = true)
    private String email;

}
