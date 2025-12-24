package com.alias.bot.game;

import java.util.List;

public record TeamState(
        Long teamId,
        String name,
        List<Long> players,
        int score
) {
    public TeamState withScore(int newScore) {
        return new TeamState(teamId, name, players, newScore);
    }
}
