package com.alias.game.service;

import com.alias.game.entity.GameEntity;
import com.alias.game.entity.PlayerEntity;
import com.alias.game.entity.TeamEntity;
import com.alias.game.model.Round;
import com.alias.game.repository.GameRepository;
import com.alias.game.repository.PlayerRepository;
import com.alias.game.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    // Очередь загадывающих: ключ gameId:teamId
    private final Map<String, Queue<Long>> guesserQueues = new HashMap<>();
    private final Map<Long, Round> currentRounds = new HashMap<>();
    private final Map<Long, List<String>> gameWords = new HashMap<>();

    private final List<String> defaultWordList = Arrays.asList(
            "Слон", "Кот", "Ракета", "Дерево", "Компьютер",
            "Птица", "Машина", "Рыба", "Дом", "Солнце"
    );

    // ==============================
    // Создание игры и команд
    // ==============================
    public GameEntity createGame(Long creatorId, int teamCount) {
        GameEntity game = GameEntity.builder()
                .creatorId(creatorId)
                .teamCount(teamCount)
                .state("PENDING_READY")
                .build();
        gameRepository.save(game);

        for (int i = 1; i <= teamCount; i++) {
            TeamEntity team = TeamEntity.builder()
                    .game(game)
                    .name("Team " + i)
                    .score(0)
                    .build();
            teamRepository.save(team);
        }

        gameWords.put(game.getId(), new ArrayList<>(defaultWordList));
        return game;
    }

    public PlayerEntity joinGame(Long gameId, Long playerId, String username) {
        GameEntity game = gameRepository.findById(gameId).orElseThrow();
        PlayerEntity player = PlayerEntity.builder()
                .telegramId(playerId)
                .username(username)
                .game(game)
                .build();
        playerRepository.save(player);
        return player;
    }

    public void shuffleTeams(Long gameId) {
        List<PlayerEntity> players = playerRepository.findByGameId(gameId);
        List<TeamEntity> teams = teamRepository.findByGameId(gameId);

        Collections.shuffle(players);
        int teamCount = teams.size();
        for (int i = 0; i < players.size(); i++) {
            PlayerEntity player = players.get(i);
            TeamEntity team = teams.get(i % teamCount);
            player.setTeam(team);
            playerRepository.save(player);
        }
    }

    // ==============================
    // Раунды
    // ==============================
    public Round startRound(Long gameId, Long teamId) {
        String key = gameId + ":" + teamId;
        TeamEntity team = teamRepository.findById(teamId).orElseThrow();
        Long guesserId = getNextGuesser(gameId, teamId);

        Queue<String> wordsQueue = new LinkedList<>(gameWords.get(gameId));
        Round round = new Round(gameId, teamId, guesserId, 0, wordsQueue);
        currentRounds.put(gameId, round);

        return round;
    }

    public String nextWord(Long gameId) {
        Round round = currentRounds.get(gameId);
        if (round == null || round.getWordsQueue().isEmpty()) return null;
        return round.getWordsQueue().poll();
    }

    public void guess(Long gameId) {
        Round round = currentRounds.get(gameId);
        if (round == null) return;

        round.setRoundScore(round.getRoundScore() + 1);

        TeamEntity team = teamRepository.findById(round.getTeamId()).orElseThrow();
        team.setScore(team.getScore() + 1);
        teamRepository.save(team);
    }

    public void skip(Long gameId) {
        Round round = currentRounds.get(gameId);
        if (round == null || round.getWordsQueue().isEmpty()) return;

        round.getWordsQueue().add(round.getWordsQueue().poll());
    }

    public boolean isGameFinished(Long gameId) {
        List<TeamEntity> teams = teamRepository.findByGameId(gameId);
        GameEntity game = gameRepository.findById(gameId).orElseThrow();
        for (TeamEntity team : teams) {
            if (team.getScore() >= game.getMaxScore()) {
                game.setState("FINISHED");
                gameRepository.save(game);
                return true;
            }
        }
        return false;
    }

    private Long getNextGuesser(Long gameId, Long teamId) {
        String key = gameId + ":" + teamId;
        Queue<Long> queue = guesserQueues.computeIfAbsent(key, k -> new LinkedList<>());

        if (queue.isEmpty()) {
            List<PlayerEntity> teamPlayers = playerRepository.findByTeamId(teamId);
            for (PlayerEntity p : teamPlayers) queue.add(p.getId());
        }

        Long guesserId = queue.poll();
        queue.offer(guesserId);
        return guesserId;
    }

    // ==============================
    // Публичные методы для TelegramService
    // ==============================
    public List<Long> getPlayersInGame(Long gameId) {
        List<PlayerEntity> players = playerRepository.findByGameId(gameId);
        List<Long> ids = new ArrayList<>();
        for (PlayerEntity p : players) ids.add(p.getTelegramId());
        return ids;
    }

    public List<Long> getTeamIds(Long gameId) {
        List<TeamEntity> teams = teamRepository.findByGameId(gameId);
        List<Long> ids = new ArrayList<>();
        for (TeamEntity t : teams) ids.add(t.getId());
        return ids;
    }

    public List<Long> getPlayerIdsInTeam(Long teamId) {
        List<PlayerEntity> players = playerRepository.findByTeamId(teamId);
        List<Long> ids = new ArrayList<>();
        for (PlayerEntity p : players) ids.add(p.getTelegramId());
        return ids;
    }

    public String getPlayerUsername(Long playerId) {
        return playerRepository.findById(playerId).orElseThrow().getUsername();
    }

    public Round getCurrentRound(Long gameId) {
        return currentRounds.get(gameId);
    }

    public void removeCurrentRound(Long gameId) {
        currentRounds.remove(gameId);
    }
}
