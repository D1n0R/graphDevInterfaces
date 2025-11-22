package com.alias.bot.model;

import com.alias.common.model.Round;

public class TelegramUserSession {
    private Long chatId;
    private Long gameId;
    private Long teamId;
    private Long playerId;
    private Round currentRound;

    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }

    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }

    public Round getCurrentRound() { return currentRound; }
    public void setCurrentRound(Round currentRound) { this.currentRound = currentRound; }
}
