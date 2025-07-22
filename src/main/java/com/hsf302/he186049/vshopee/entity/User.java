package com.hsf302.he186049.vshopee.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    private String fullname;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    private String address;

    private Boolean banned = false;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
