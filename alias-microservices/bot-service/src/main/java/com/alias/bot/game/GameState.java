package com.alias.bot.game;

import java.util.List;
import java.util.Map;

public class GameState {

    private List<Map<String, Object>> teams;
    private Map<Long, List<Long>> teamPlayers;

    private int currentTeamIndex = 0;
    private Long currentExplainerId;
    private String currentWord;
    private boolean active = false;

    public void start(List<Map<String, Object>> teams, Map<Long, List<Long>> teamPlayers) {
        this.teams = teams;
        this.teamPlayers = teamPlayers;
        this.currentTeamIndex = 0;
        this.active = true;
    }

    public boolean isActive() { return active; }
    public void stop() { this.active = false; }

    public Map<String, Object> currentTeam() { return teams.get(currentTeamIndex); }
    public Long currentTeamId() { return ((Number) currentTeam().get("id")).longValue(); }

    public void nextTeam() { currentTeamIndex = (currentTeamIndex + 1) % teams.size(); }

    public void setCurrentExplainerId(Long id) { this.currentExplainerId = id; }
    public Long getCurrentExplainerId() { return currentExplainerId; }

    public void setCurrentWord(String word) { this.currentWord = word; }
    public String getCurrentWord() { return currentWord; }

    public List<Map<String, Object>> getTeams() { return teams; }

    public boolean isExplainer(Long telegramId) {
        return telegramId.equals(currentExplainerId);
    }
}
