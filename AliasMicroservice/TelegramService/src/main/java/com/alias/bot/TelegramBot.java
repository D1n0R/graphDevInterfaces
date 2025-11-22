package com.alias.bot;

import com.alias.bot.service.TelegramService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final TelegramService telegramService;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            System.out.println("Telegram бот зарегистрирован и запущен!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            String response;

            switch (messageText.split(" ")[0].toLowerCase()) {
                case "/start":
                    response = "Привет! Используй команды: /newgame, /join <team>, /startround, /guess или /skip";
                    break;
                case "/newgame":
                    response = telegramService.createGame(chatId);
                    break;
                case "/join":
                    String[] parts = messageText.split(" ");
                    if (parts.length < 2) {
                        response = "Укажи команду: /join <teamId>";
                    } else {
                        Long teamId = Long.parseLong(parts[1]);
                        response = telegramService.joinTeam(chatId, teamId);
                    }
                    break;
                case "/startround":
                    response = telegramService.startRound(chatId);
                    break;
                case "/guess":
                    response = telegramService.guessWord(chatId);
                    break;
                case "/skip":
                    response = telegramService.skipWord(chatId);
                    break;
                default:
                    response = "Неизвестная команда. Используй /newgame, /join <team>, /startround, /guess или /skip";
            }

            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText(response);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
