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

    public void startGameSession(Long chatId, Long gameId, Long playerId, Long teamId) {
        TelegramUserSession session = new TelegramUserSession();
        session.setChatId(chatId);
        session.setGameId(gameId);
        session.setPlayerId(playerId);
        session.setTeamId(teamId);

        Round currentRound = gameServiceClient.startRound(gameId, teamId);
        session.setCurrentRound(currentRound);

        sessions.put(chatId, session);
    }

    public String guessWord(Long chatId) {
        TelegramUserSession session = sessions.get(chatId);
        if (session == null) return "Сессия не найдена";

        gameServiceClient.guess(session.getGameId());
        // Обновляем текущий раунд
        Round round = gameServiceClient.getCurrentRound(session.getGameId());
        session.setCurrentRound(round);

        return "Слово угадано!";
    }

    public String skipWord(Long chatId) {
        TelegramUserSession session = sessions.get(chatId);
        if (session == null) return "Сессия не найдена";

        gameServiceClient.skip(session.getGameId());
        Round round = gameServiceClient.getCurrentRound(session.getGameId());
        session.setCurrentRound(round);

        return "Слово пропущено!";
    }

    public Round getCurrentRound(Long chatId) {
        TelegramUserSession session = sessions.get(chatId);
        if (session == null) return null;

        Round round = gameServiceClient.getCurrentRound(session.getGameId());
        session.setCurrentRound(round);
        return round;
    }
}
