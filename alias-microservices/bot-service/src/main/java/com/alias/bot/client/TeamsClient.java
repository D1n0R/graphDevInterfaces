package com.alias.bot.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class TeamsClient {

    private final RestTemplate rest = new RestTemplate();

    @Value("${teams.service.url}")
    private String baseUrl;

    public List<Map<String, Object>> generateTeams(List<Long> userIds, int teamsCount) {
        return rest.postForObject(
                baseUrl + "/teams/generate?teamsCount=" + teamsCount,
                userIds,
                List.class
        );
    }

    public void addScore(Long teamId) {
        rest.postForLocation(baseUrl + "/teams/" + teamId + "/score", null);
    }

    public String word() {
        return rest.postForObject(baseUrl + "/game/word", null, String.class);
    }
}
