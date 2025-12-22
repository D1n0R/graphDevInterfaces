package com.alias.teams.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TEAMS")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int score;

    public Team() {}

    public Team(String name) {
        this.name = name;
        this.score = 0;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public int getScore() { return score; }

    public void incrementScore() {
        this.score++;
    }
}
