package com.alias.users.model;

import jakarta.persistence.*;

@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long telegramId;

    @Column(nullable = false)
    private String name;

    public User() {}

    public User(Long telegramId, String name) {
        this.telegramId = telegramId;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public String getName() {
        return name;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public void setName(String name) {
        this.name = name;
    }
}
