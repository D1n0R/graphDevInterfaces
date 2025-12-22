package com.alias.users.controller;

import com.alias.users.model.User;
import com.alias.users.repo.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public ResponseEntity<User> register(@RequestBody User user) {
        // проверка на дубликат по telegramId
        if (repo.findByTelegramId(user.getTelegramId()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.save(user));
    }

    @GetMapping
    public List<User> list() {
        return repo.findAll();
    }

    @DeleteMapping
    public void clear() {
        repo.deleteAll();
    }
}
