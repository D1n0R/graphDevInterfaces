package com.alias.game.controller;

import com.alias.game.model.Round;
import com.alias.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    // Получить текущий раунд
    @GetMapping("/currentRound/{gameId}")
    public Round getCurrentRound(@PathVariable Long gameId) {
        return gameService.getCurrentRound(gameId);
    }

    // Игрок угадал слово
    @PostMapping("/guess/{gameId}")
    public void guess(@PathVariable Long gameId) {
        gameService.guess(gameId);
    }

    // Игрок пропустил слово
    @PostMapping("/skip/{gameId}")
    public void skip(@PathVariable Long gameId) {
        gameService.skip(gameId);
    }

    // Начать раунд
    @PostMapping("/startRound")
    public Round startRound(@RequestParam Long gameId, @RequestParam Long teamId) {
        return gameService.startRound(gameId, teamId);
    }

    // Получить список игроков в игре
    @GetMapping("/players/{gameId}")
    public List<Long> getPlayersInGame(@PathVariable Long gameId) {
        return gameService.getPlayersInGame(gameId);
    }

    // Получить список команд
    @GetMapping("/teams/{gameId}")
    public List<Long> getTeamIds(@PathVariable Long gameId) {
        return gameService.getTeamIds(gameId);
    }

    // Получить игроков в команде
    @GetMapping("/teamPlayers/{teamId}")
    public List<Long> getPlayerIdsInTeam(@PathVariable Long teamId) {
        return gameService.getPlayerIdsInTeam(teamId);
    }

    // Получить username игрока
    @GetMapping("/username/{playerId}")
    public String getPlayerUsername(@PathVariable Long playerId) {
        return gameService.getPlayerUsername(playerId);
    }

    // Проверка окончания игры
    @GetMapping("/isFinished/{gameId}")
    public boolean isGameFinished(@PathVariable Long gameId) {
        return gameService.isGameFinished(gameId);
    }
}
