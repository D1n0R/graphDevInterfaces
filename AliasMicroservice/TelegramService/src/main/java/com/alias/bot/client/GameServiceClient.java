package com.alias.bot.client;

import com.alias.game.model.Round;

import java.util.List;

public interface GameServiceClient {

    List<Long> getPlayersInGame(Long gameId);

    List<Long> getTeamIds(Long gameId);

    List<Long> getPlayerIdsInTeam(Long teamId);

    String getPlayerUsername(Long playerId);

    Round startRound(Long gameId, Long teamId);

    String nextWord(Long gameId);

    Round getCurrentRound(Long gameId);

    void guess(Long gameId);

    void skip(Long gameId);

    boolean isGameFinished(Long gameId);
}
