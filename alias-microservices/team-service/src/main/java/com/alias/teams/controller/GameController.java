package com.alias.teams.controller;

import com.alias.teams.model.Word;
import com.alias.teams.repo.WordRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/game")
public class GameController {

    private final WordRepository repo;
    private final Random random = new Random();

    public GameController(WordRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/word")
    public String randomWord() {
        List<Word> words = repo.findAll();
        return words.get(random.nextInt(words.size())).getWord();
    }
}
