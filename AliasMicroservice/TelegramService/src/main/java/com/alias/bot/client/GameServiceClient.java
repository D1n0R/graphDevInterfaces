package com.alias.bot.client;

import com.alias.common.model.Round;

import java.util.List;

public interface GameServiceClient {

    Round startRound(Long gameId, Long teamId);

    Round getCurrentRound(Long gameId);

    void guess(Long gameId);

    void skip(Long gameId);

    List<Long> getTeamIds(Long gameId);

    List<Long> getPlayersInGame(Long gameId);

    String getPlayerUsername(Long playerId);
}
