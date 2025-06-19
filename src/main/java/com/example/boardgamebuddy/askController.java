package com.example.boardgamebuddy;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class askController {

    private final BoardGameService boardGameService;

    public askController(BoardGameService boardGameService) {
        this.boardGameService = boardGameService;
    }

    @PostMapping(path="ask", produces="application/json")
    public Answer ask(@RequestBody Question question) {
        return boardGameService.askQuestion(question);
    }
}
