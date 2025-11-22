package com.alias.game.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "game")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long creatorId;

    private String state; // PENDING_READY, IN_PROGRESS, FINISHED

    private Integer teamCount;

    private Integer maxScore = 20;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<TeamEntity> teams;
}
