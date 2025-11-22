package com.alias.game.controller;

import com.alias.game.dto.CreateGameRequest;
import com.alias.game.dto.JoinGameRequest;
import com.alias.game.entity.GameEntity;
import com.alias.game.entity.PlayerEntity;
import com.alias.game.model.Round;
import com.alias.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/create")
    public GameEntity createGame(@RequestBody CreateGameRequest request) {
        return gameService.createGame(request.getCreatorId(), request.getTeamCount());
    }

    @PostMapping("/join")
    public PlayerEntity joinGame(@RequestBody JoinGameRequest request) {
        return gameService.joinGame(request.getGameId(), request.getPlayerId(), request.getUsername());
    }

    @PostMapping("/shuffle")
    public void shuffle(@RequestParam Long gameId) {
        gameService.shuffleTeams(gameId);
    }

    @PostMapping("/startRound")
    public Round startRound(@RequestParam Long gameId, @RequestParam Long teamId) {
        return gameService.startRound(gameId, teamId);
    }

    @PostMapping("/nextWord")
    public String nextWord(@RequestParam Long gameId) {
        return gameService.nextWord(gameId);
    }

    @PostMapping("/guess")
    public void guess(@RequestParam Long gameId) {
        gameService.guess(gameId);
    }

    @PostMapping("/skip")
    public void skip(@RequestParam Long gameId) {
        gameService.skip(gameId);
    }

    @GetMapping("/isFinished")
    public boolean isFinished(@RequestParam Long gameId) {
        return gameService.isGameFinished(gameId);
    }

}
