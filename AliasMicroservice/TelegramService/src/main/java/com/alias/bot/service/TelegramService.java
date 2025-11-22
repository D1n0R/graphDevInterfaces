package com.alias.bot.service;

import com.alias.bot.client.GameServiceClient;
import com.alias.bot.model.TelegramUserSession;
import com.alias.common.model.Round;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TelegramService {

    private final GameServiceClient gameServiceClient;
    private final Map<Long, TelegramUserSession> sessions = new HashMap<>();

    public String createGame(Long chatId) {
        TelegramUserSession session = getOrCreateSession(chatId);
        Long gameId = gameServiceClient.createGame(); // создаем игру через GameService
        session.setGameId(gameId);
        return "Игра создана! Твой gameId: " + gameId + ". Присоединяйся к команде: /join <teamId>";
    }

    public String joinTeam(Long chatId, Long teamId) {
        TelegramUserSession session = getOrCreateSession(chatId);
        session.setTeamId(teamId);
        Long playerId = gameServiceClient.addPlayerToTeam(session.getGameId(), teamId);
        session.setPlayerId(playerId);
        return "Ты присоединился к команде " + teamId + ". Начни раунд: /startround";
    }

    public String startRound(Long chatId) {
        TelegramUserSession session = getOrCreateSession(chatId);
        if (session.getGameId() == null || session.getTeamId() == null) {
            return "Сначала создай игру и присоединяйся к команде!";
        }
        Round round = gameServiceClient.startRound(session.getGameId(), session.getTeamId());
        session.setCurrentRound(round);
        return "Раунд начат! Загадано первое слово.";
    }


    private TelegramUserSession getOrCreateSession(Long chatId) {
        return sessions.computeIfAbsent(chatId, k -> new TelegramUserSession());
    }

    public String guessWord(Long chatId) {
        TelegramUserSession session = getOrCreateSession(chatId);
        Round round = session.getCurrentRound();
        if (round == null) return "Нет активного раунда. Сначала создайте игру.";

        gameServiceClient.guess(round.getGameId());
        round.setRoundScore(round.getRoundScore() + 1);

        return "Слово угадано! Текущий счет команды: " + round.getRoundScore();
    }

    public String skipWord(Long chatId) {
        TelegramUserSession session = getOrCreateSession(chatId);
        Round round = session.getCurrentRound();
        if (round == null) return "Нет активного раунда. Сначала создайте игру.";

        gameServiceClient.skip(round.getGameId());

        return "Слово пропущено!";
    }

    public String startGame(Long chatId, Long gameId, Long teamId, Long playerId) {
        TelegramUserSession session = getOrCreateSession(chatId);
        session.setGameId(gameId);
        session.setTeamId(teamId);
        session.setPlayerId(playerId);

        Round round = gameServiceClient.startRound(gameId, teamId);
        session.setCurrentRound(round);

        return "Раунд начат! Загадано первое слово.";
    }

}
