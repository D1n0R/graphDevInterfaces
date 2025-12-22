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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AliasTelegramBot extends TelegramLongPollingBot {

    private final UsersClient usersClient;
    private final TeamsClient teamsClient;
    private final GameState gameState = new GameState();

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

        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        try {
            if (text.startsWith("/join")) {
                usersClient.register(
                        update.getMessage().getFrom().getId(),
                        update.getMessage().getFrom().getFirstName()
                );
                send(chatId, "Ты в игре!");
            }

            if (text.startsWith("/start_game")) {
                int teamsCount = Integer.parseInt(text.split(" ")[1]);

                List<Map<String, Object>> users = usersClient.users();
                List<Long> userIds = users.stream()
                        .map(u -> ((Number) u.get("id")).longValue())
                        .toList();

                List<Map<String, Object>> teams =
                        teamsClient.generateTeams(userIds, teamsCount);

                gameState.start(
                        teams.stream()
                                .map(t -> ((Number) t.get("id")).longValue())
                                .toList()
                );

                send(chatId, "Команды созданы. Ходит команда " + teams.get(0).get("name"));
            }

            if (text.equals("/word")) {
                send(chatId, teamsClient.word());
            }

            if (text.equals("/point")) {
                teamsClient.addScore(gameState.currentTeam());
                gameState.nextTeam();
                send(chatId, "Очко! Теперь ходит следующая команда");
            }

        } catch (Exception e) {
            try {
                send(chatId, "Ошибка: " + e.getMessage());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void send(Long chatId, String text) throws Exception {
        execute(new SendMessage(chatId.toString(), text));
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
