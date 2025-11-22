package com.alias.bot.client;

import com.alias.common.model.Round;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class GameServiceClientImpl implements GameServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String baseUrl = "http://localhost:8080/api/game"; // URL GameService

    @Override
    public Round startRound(Long gameId, Long teamId) {
        return restTemplate.postForObject(baseUrl + "/round/start?gameId={gameId}&teamId={teamId}",
                null, Round.class, gameId, teamId);
    }

    @Override
    public Round getCurrentRound(Long gameId) {
        return restTemplate.getForObject(baseUrl + "/round/current?gameId={gameId}", Round.class, gameId);
    }

    @Override
    public void guess(Long gameId) {
        restTemplate.postForLocation(baseUrl + "/round/guess?gameId={gameId}", null, gameId);
    }

    @Override
    public void skip(Long gameId) {
        restTemplate.postForLocation(baseUrl + "/round/skip?gameId={gameId}", null, gameId);
    }

    @Override
    public List<Long> getTeamIds(Long gameId) {
        return restTemplate.getForObject(baseUrl + "/teams?gameId={gameId}", List.class, gameId);
    }

    @Override
    public List<Long> getPlayersInGame(Long gameId) {
        return restTemplate.getForObject(baseUrl + "/players?gameId={gameId}", List.class, gameId);
    }

    @Override
    public String getPlayerUsername(Long playerId) {
        return restTemplate.getForObject(baseUrl + "/player/{playerId}/username", String.class, playerId);
    }
}
