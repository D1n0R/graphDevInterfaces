package com.alias.bot.client;

import com.alias.common.model.Round;
import com.alias.common.dto.PlayerDto;
import com.alias.common.dto.GameDto;
import com.alias.common.dto.TeamDto;

import java.util.List;

public interface GameServiceClient {

    GameDto createGame();

    PlayerDto joinGame(Long gameId, Long playerId, String username);

    void shuffleTeams(Long gameId);

    Round startRound(Long gameId, Long teamId);

    String nextWord(Long gameId);

    void guess(Long gameId);

    void skip(Long gameId);

    boolean isGameFinished(Long gameId);

    List<Long> getTeamIds(Long gameId);

    List<PlayerDto> getPlayersInTeam(Long teamId);

    int getTeamScore(Long teamId);
}
