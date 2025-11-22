package com.alias.game.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "team")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private GameEntity game;

    private String name;

    private Integer score = 0;

    private String color;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<PlayerEntity> players;
}
