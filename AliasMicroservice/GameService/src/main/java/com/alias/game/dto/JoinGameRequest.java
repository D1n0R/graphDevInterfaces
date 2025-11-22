package com.alias.game.dto;

import lombok.Data;

@Data
public class JoinGameRequest {
    private Long gameId;
    private Long playerId;
    private String username;
}
