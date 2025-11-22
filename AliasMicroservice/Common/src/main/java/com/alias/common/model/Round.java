package com.alias.common.model;

import java.util.Queue;

public class Round {
    private Long gameId;
    private Long teamId;
    private Long guesserId;
    private int roundScore;
    private Queue<String> wordsQueue;

    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }

    public Long getGuesserId() { return guesserId; }
    public void setGuesserId(Long guesserId) { this.guesserId = guesserId; }

    public int getRoundScore() { return roundScore; }
    public void setRoundScore(int roundScore) { this.roundScore = roundScore; }

    public Queue<String> getWordsQueue() { return wordsQueue; }
    public void setWordsQueue(Queue<String> wordsQueue) { this.wordsQueue = wordsQueue; }
}
