package com.alias.bot.service;

import com.alias.bot.client.GameServiceClient;
import com.alias.bot.model.TelegramUserSession;
import com.alias.game.model.Round;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TelegramService {

    private final GameServiceClient gameClient;

    private final Map<Long, TelegramUserSession> sessions = new ConcurrentHashMap<>();
    private final Map<Long, Set<Long>> readyPlayers = new ConcurrentHashMap<>();
    private final Map<Long, Timer> roundTimers = new ConcurrentHashMap<>();

    public void handleReady(Long gameId, Long playerId) {
        readyPlayers.computeIfAbsent(gameId, k -> new HashSet<>()).add(playerId);
        List<Long> allPlayers = gameClient.getPlayersInGame(gameId);

        if (readyPlayers.get(gameId).containsAll(allPlayers)) {
            broadcastToGame(gameId, "Все игроки готовы! Раунд начнётся через 3 секунды...");
            startRoundWithDelay(gameId, 3000);
        }
    }

    private void startRoundWithDelay(Long gameId, long delayMillis) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startNextRound(gameId);
            }
        }, delayMillis);
    }

    private void startNextRound(Long gameId) {
        List<Long> teamIds = gameClient.getTeamIds(gameId);
        for (Long teamId : teamIds) {
            Round round = gameClient.startRound(gameId, teamId);
            broadcastToTeam(teamId, "Раунд начинается! Загадал: " +
                    gameClient.getPlayerUsername(round.getGuesserId()));

            startRoundTimer(gameId, 60_000);
            sendNextWord(gameId);
            break;
        }
    }

    private void sendNextWord(Long gameId) {
        Round round = gameClient.getCurrentRound(gameId);
        if (round == null) return;

        String word = gameClient.nextWord(gameId);
        if (word == null) {
            endRound(gameId);
            return;
        }

        sendMessageToGuesser(round.getGuesserId(), "Загадываю слово: " + word + "\n[Угадано] [Пропуск]");
    }

    public void handleGuess(Long gameId) {
        gameClient.guess(gameId);
        sendNextWord(gameId);
    }

    public void handleSkip(Long gameId) {
        gameClient.skip(gameId);
        sendNextWord(gameId);
    }

    private void startRoundTimer(Long gameId, long millis) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                endRound(gameId);
            }
        }, millis);
        roundTimers.put(gameId, timer);
    }

    private void endRound(Long gameId) {
        Timer t = roundTimers.remove(gameId);
        if (t != null) t.cancel();

        broadcastToGame(gameId, "Раунд окончен!");

        if (gameClient.isGameFinished(gameId)) {
            broadcastToGame(gameId, "Игра окончена!");
        } else {
            startNextRound(gameId);
        }
    }

    private void broadcastToGame(Long gameId, String text) {
        List<Long> players = gameClient.getPlayersInGame(gameId);
        players.forEach(pid -> sendMessage(pid, text));
    }

    private void broadcastToTeam(Long teamId, String text) {
        List<Long> players = gameClient.getPlayerIdsInTeam(teamId);
        players.forEach(pid -> sendMessage(pid, text));
    }

    private void sendMessageToGuesser(Long telegramId, String text) {
        sendMessage(telegramId, text);
    }

    private void sendMessage(Long telegramId, String text) {
        // Реализация Telegram Bot API
        // SendMessage message = new SendMessage();
        // message.setChatId(telegramId.toString());
        // message.setText(text);
        // execute(message);
    }
}
