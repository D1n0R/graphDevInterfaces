package com.alias.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TelegramUserSession {

    private Long telegramId;        // Telegram ID игрока
    private Long gameId;            // ID текущей игры
    private Long teamId;            // ID команды
    private boolean ready;          // Готовность игрока
    private Long currentGuesserId;  // ID текущего загадывающего
}
