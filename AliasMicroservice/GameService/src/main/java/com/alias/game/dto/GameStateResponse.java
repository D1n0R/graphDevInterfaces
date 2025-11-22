package com.alias.game.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GameStateResponse {
    private Long gameId;
    private String state;
    private Map<Long, Integer> teamScores; // teamId -> score
    private Map<Long, List<Long>> teamPlayers; // teamId -> list of playerIds
}
