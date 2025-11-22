package com.alias.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDto {
    private Long id;
    private Long telegramId;
    private String username;
    private Long gameId;
    private Long teamId;
}
