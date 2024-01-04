package com.example.fulbrincjava.entities;

import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import lombok.Setter;
import lombok.Getter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String pass;

    @Column(length = 1024)
    private String about;

    @Column(length = 1024)
    private String contact;
    public void setPass(String pass) {
        this.pass = new BCryptPasswordEncoder().encode(pass);
    }
}
