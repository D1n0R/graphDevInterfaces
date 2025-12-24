package com.alias.teams.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TEAM_MEMBERS")
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;   // ID из users-service

    @ManyToOne
    private Team team;

    public TeamMember() {}

    public TeamMember(Long userId, Team team) {
        this.userId = userId;
        this.team = team;
    }

    public Long getUserId() { return userId; }
    public Team getTeam() { return team; }
}
