package com.alias.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Queue;

@Data
@AllArgsConstructor
public class Round {
    private Long gameId;
    private Long teamId;
    private Long guesserId;
    private int roundScore;
    private Queue<String> wordsQueue; // слова текущего раунда
}
