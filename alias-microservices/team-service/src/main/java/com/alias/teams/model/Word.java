package com.alias.teams.model;

import jakarta.persistence.*;

@Entity
@Table(name = "WORDS")
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String word;

    public Word() {}

    public Word(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
