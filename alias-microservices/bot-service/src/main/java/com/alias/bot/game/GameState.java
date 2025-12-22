package com.alias.bot.game;

import java.util.List;

public class GameState {

    private List<Long> teamIds;
    private int currentTeamIndex = 0;

    public void start(List<Long> teamIds) {
        this.teamIds = teamIds;
        this.currentTeamIndex = 0;
    }

    public Long currentTeam() {
        return teamIds.get(currentTeamIndex);
    }

    public void nextTeam() {
        currentTeamIndex = (currentTeamIndex + 1) % teamIds.size();
    }
}
