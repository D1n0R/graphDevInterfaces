package com.alias.bot.bot;

import com.alias.bot.client.TeamsClient;
import com.alias.bot.client.UsersClient;
import com.alias.bot.game.GameState;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
public class AliasTelegramBot extends TelegramLongPollingBot {

    private final UsersClient usersClient;
    private final TeamsClient teamsClient;
    private final GameState gameState = new GameState();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> currentScheduledFuture;

    /**
     * telegramUserId -> chatId
     */
    private final Map<Long, Long> userChats = new ConcurrentHashMap<>();
    /**
     * teamId -> telegramUserIds
     */
    private final Map<Long, List<Long>> teamPlayers = new ConcurrentHashMap<>();
    /**
     * chatId, где была запущена игра
     */
    private volatile Long gameChatId;

    @Value("${bot.token}")
    private String token;
    @Value("${bot.username}")
    private String username;

    public AliasTelegramBot(UsersClient usersClient, TeamsClient teamsClient) {
        this.usersClient = usersClient;
        this.teamsClient = teamsClient;
    }

    @PostConstruct
    public void init() throws Exception {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        userChats.put(userId, chatId);

        String text = update.getMessage().getText();

        try {
            if (text.startsWith("/join")) {
                usersClient.register(userId, update.getMessage().getFrom().getFirstName());
                send(chatId, "Ты в игре!");
            } else if (text.startsWith("/start_game")) {
                startGame(chatId, text);
            } else if (text.equalsIgnoreCase("Следующее слово")) {
                handleNextWord(userId, chatId);
            } else if (text.equalsIgnoreCase("Угадали")) {
                handleGuessed(userId, chatId);
            } else if (text.equalsIgnoreCase("Пропуск")) {
                handleSkip(userId, chatId);
            }

        } catch (Exception e) {
            safeSend(chatId, "Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private synchronized void startGame(Long chatId, String text) throws Exception {
        this.gameChatId = chatId;

        if (gameState.isActive()) {
            send(chatId, "Игра уже идет!");
            return;
        }

        int teamsCount = Integer.parseInt(text.split(" ")[1]);

        List<Map<String, Object>> users = usersClient.users();
        List<Long> telegramIds = new ArrayList<>();
        Map<Long, Long> userIdToTelegram = new HashMap<>();

        for (Map<String, Object> u : users) {
            Long tgId = ((Number) u.get("telegramId")).longValue();
            Long uid = ((Number) u.get("id")).longValue();
            telegramIds.add(tgId);
            userIdToTelegram.put(uid, tgId);
        }

        // Генерация команд
        List<Map<String, Object>> teams = teamsClient.generateTeams(
                new ArrayList<>(userIdToTelegram.keySet()), teamsCount
        );

        // Распределяем telegramId игроков по командам
        teamPlayers.clear();
        for (int i = 0; i < teams.size(); i++) {
            Long teamId = ((Number) teams.get(i).get("id")).longValue();
            teamPlayers.put(teamId, new ArrayList<>());
        }

        for (int i = 0; i < telegramIds.size(); i++) {
            Long teamId = ((Number) teams.get(i % teams.size()).get("id")).longValue();
            teamPlayers.get(teamId).add(telegramIds.get(i));
        }

        gameState.start(teams, teamPlayers);
        send(chatId, "Команды созданы. Начинаем игру!");
        startRound();
    }

    private synchronized void startRound() {
        if (!gameState.isActive()) return;

        // Сообщение о составах команд
        StringBuilder sb = new StringBuilder("Состав команд:\n");
        for (Map<String, Object> team : gameState.getTeams()) {
            Long teamId = ((Number) team.get("id")).longValue();
            List<Long> players = teamPlayers.getOrDefault(teamId, List.of());
            sb.append(team.get("name"))
                    .append(": ")
                    .append(players.isEmpty() ? "нет игроков" : players)
                    .append("\n");
        }
        safeSend(gameChatId, sb.toString());

        // Текущая команда
        Map<String, Object> currentTeam = gameState.currentTeam();
        List<Long> currentTeamPlayers = teamPlayers.get(currentTeamId());
        if (currentTeamPlayers == null || currentTeamPlayers.isEmpty()) {
            safeSend(gameChatId, "Команда не имеет игроков. Пропуск хода.");
            nextTurn();
            return;
        }

        // Выбираем объясняющего (первого в списке)
        Long explainerId = currentTeamPlayers.get(0);
        gameState.setCurrentExplainerId(explainerId);

        safeSend(gameChatId, "Ход команды: " + currentTeam.get("name"));
        safeSend(explainerId, "Ты объясняющий. Нажми 'Следующее слово' для начала.");

        startTimer();
    }

    private void handleNextWord(Long userId, Long chatId) {
        if (!userId.equals(gameState.getCurrentExplainerId())) {
            send(chatId, "Сейчас не ваш ход");
            return;
        }

        String word = teamsClient.word();
        gameState.setCurrentWord(word);
        send(userId, "Слово для объяснения: " + word);
    }

    private void handleGuessed(Long userId, Long chatId) {
        if (!userId.equals(gameState.getCurrentExplainerId())) {
            send(chatId, "Сейчас не ваш ход");
            return;
        }

        Long teamId = currentTeamId();
        teamsClient.addScore(teamId);

        broadcastScores();
        nextTurn();
    }

    private void handleSkip(Long userId, Long chatId) {
        if (!userId.equals(gameState.getCurrentExplainerId())) {
            send(chatId, "Сейчас не ваш ход");
            return;
        }

        safeSend(chatId, "Слово пропущено");
        nextTurn();
    }

    private void broadcastScores() {
        StringBuilder sb = new StringBuilder("Текущий счет:\n");
        for (Map<String, Object> team : gameState.getTeams()) {
            sb.append(team.get("name"))
                    .append(": ")
                    .append(team.get("score"))
                    .append("\n");
        }

        List<Long> allPlayers = new ArrayList<>();
        for (List<Long> pl : teamPlayers.values()) allPlayers.addAll(pl);
        broadcastToPlayers(allPlayers, sb.toString());
    }

    private void nextTurn() {
        cancelTimer();
        gameState.nextTeam();
        startRound();
    }

    private void startTimer() {
        cancelTimer();
        currentScheduledFuture = scheduler.schedule(() -> {
            Long teamId = currentTeamId();
            teamsClient.addScore(teamId);

            broadcastScores();
            nextTurn();
        }, 1, TimeUnit.MINUTES);
    }

    private void cancelTimer() {
        if (currentScheduledFuture != null && !currentScheduledFuture.isDone()) {
            currentScheduledFuture.cancel(false);
        }
    }

    private Long currentTeamId() {
        return ((Number) gameState.currentTeam().get("id")).longValue();
    }

    private void broadcastToPlayers(List<Long> telegramIds, String message) {
        for (Long id : telegramIds) {
            safeSend(id, message);
        }
    }

    private void safeSend(Long chatId, String text) {
        try {
            if (chatId != null) execute(new SendMessage(chatId.toString(), text));
        } catch (Exception ignored) {
        }
    }

    private void send(Long chatId, String text) {
        try {
            execute(new SendMessage(chatId.toString(), text));
        } catch (Exception ignored) {
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
