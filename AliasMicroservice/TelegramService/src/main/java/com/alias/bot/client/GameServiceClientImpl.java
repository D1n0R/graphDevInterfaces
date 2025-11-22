package com.alias.bot.client;

import com.alias.common.model.Round;
import com.alias.common.dto.PlayerDto;
import com.alias.common.dto.GameDto;
import com.alias.common.dto.TeamDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class GameServiceClientImpl implements GameServiceClient {

    private final RestTemplate restTemplate;

    private final String baseUrl = "http://localhost:8081/api/game"; // URL GameService

    @Override
    public GameDto createGame(Long creatorId, int teamCount) {
        return restTemplate.postForObject(baseUrl + "/create?creatorId={creatorId}&teamCount={teamCount}",
                null, GameDto.class, creatorId, teamCount);
    }

    @Override
    public PlayerDto joinGame(Long gameId, Long playerId, String username) {
        return restTemplate.postForObject(baseUrl + "/join?gameId={gameId}&playerId={playerId}&username={username}",
                null, PlayerDto.class, gameId, playerId, username);
    }

    @Override
    public void shuffleTeams(Long gameId) {
        restTemplate.postForLocation(baseUrl + "/shuffle?gameId={gameId}", null, gameId);
    }

    @Override
    public Round startRound(Long gameId, Long teamId) {
        return restTemplate.postForObject(baseUrl + "/startRound?gameId={gameId}&teamId={teamId}",
                null, Round.class, gameId, teamId);
    }

    @Override
    public String nextWord(Long gameId) {
        return restTemplate.getForObject(baseUrl + "/nextWord?gameId={gameId}", String.class, gameId);
    }

    @Override
    public void guess(Long gameId) {
        restTemplate.postForLocation(baseUrl + "/guess?gameId={gameId}", null, gameId);
    }

    @Override
    public void skip(Long gameId) {
        restTemplate.postForLocation(baseUrl + "/skip?gameId={gameId}", null, gameId);
    }

    @Override
    public boolean isGameFinished(Long gameId) {
        return restTemplate.getForObject(baseUrl + "/isFinished?gameId={gameId}", Boolean.class, gameId);
    }

    @Override
    public List<Long> getTeamIds(Long gameId) {
        Long[] ids = restTemplate.getForObject(baseUrl + "/teamIds?gameId={gameId}", Long[].class, gameId);
        return Arrays.asList(ids);
    }

    @Override
    public List<PlayerDto> getPlayersInTeam(Long teamId) {
        PlayerDto[] players = restTemplate.getForObject(baseUrl + "/teamPlayers?teamId={teamId}", PlayerDto[].class, teamId);
        return Arrays.asList(players);
    }

    @Override
    public int getTeamScore(Long teamId) {
        return restTemplate.getForObject(baseUrl + "/teamScore?teamId={teamId}", Integer.class, teamId);
    }
}
