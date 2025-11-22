package com.alias.bot.client;

import com.alias.game.model.Round;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameServiceClientImpl implements GameServiceClient {

    private final RestTemplate restTemplate;
    private final String gameServiceUrl = "http://localhost:8081/game"; // URL GameService

    @Override
    public Round getCurrentRound(Long gameId) {
        return restTemplate.getForObject(gameServiceUrl + "/currentRound/" + gameId, Round.class);
    }

    @Override
    public void guess(Long gameId) {
        restTemplate.postForObject(gameServiceUrl + "/guess/" + gameId, null, Void.class);
    }

    @Override
    public void skip(Long gameId) {
        restTemplate.postForObject(gameServiceUrl + "/skip/" + gameId, null, Void.class);
    }

    @Override
    public Round startRound(Long gameId, Long teamId) {
        return restTemplate.postForObject(gameServiceUrl + "/startRound?gameId=" + gameId + "&teamId=" + teamId, null, Round.class);
    }

    @Override
    public List<Long> getPlayersInGame(Long gameId) {
        Long[] arr = restTemplate.getForObject(gameServiceUrl + "/players/" + gameId, Long[].class);
        return Arrays.asList(arr);
    }

    @Override
    public List<Long> getTeamIds(Long gameId) {
        Long[] arr = restTemplate.getForObject(gameServiceUrl + "/teams/" + gameId, Long[].class);
        return Arrays.asList(arr);
    }

    @Override
    public List<Long> getPlayerIdsInTeam(Long teamId) {
        Long[] arr = restTemplate.getForObject(gameServiceUrl + "/teamPlayers/" + teamId, Long[].class);
        return Arrays.asList(arr);
    }

    @Override
    public String getPlayerUsername(Long playerId) {
        return restTemplate.getForObject(gameServiceUrl + "/username/" + playerId, String.class);
    }

    @Override
    public boolean isGameFinished(Long gameId) {
        return restTemplate.getForObject(gameServiceUrl + "/isFinished/" + gameId, Boolean.class);
    }

    @Override
    public String nextWord(Long gameId) {
        return restTemplate.getForObject(gameServiceUrl + "/nextWord/" + gameId, String.class);
    }
}
