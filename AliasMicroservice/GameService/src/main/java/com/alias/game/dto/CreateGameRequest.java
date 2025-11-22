package com.alias.game.dto;

import lombok.Data;

@Data
public class CreateGameRequest {
    private Long creatorId;
    private Integer teamCount;
}
