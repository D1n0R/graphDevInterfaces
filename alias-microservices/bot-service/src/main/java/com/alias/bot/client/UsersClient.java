package com.alias.bot.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class UsersClient {

    private final RestTemplate rest = new RestTemplate();

    @Value("${users.service.url}")
    private String baseUrl;

    // Регистрация пользователя
    public void register(Long telegramId, String name) {
        rest.postForObject(
                baseUrl + "/users",
                Map.of("telegramId", telegramId, "name", name),
                Object.class
        );
    }

    // Список всех пользователей
    public List<Map<String, Object>> users() {
        return rest.getForObject(baseUrl + "/users", List.class);
    }
}
