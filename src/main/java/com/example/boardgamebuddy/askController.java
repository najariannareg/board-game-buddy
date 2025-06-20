package com.example.boardgamebuddy;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class askController {

    private final BoardGameService boardGameService;

    public askController(BoardGameService boardGameService) {
        this.boardGameService = boardGameService;
    }

    @PostMapping(path="ask", produces="application/json")
    public Answer ask(@RequestBody @Valid Question question) {
        return boardGameService.askQuestion(question);
    }

    @PostMapping(path="stream", produces="text/event-stream") // or produces="application/ndjson"
    public Flux<String> stream(@RequestBody @Valid Question question) {
        return boardGameService.streamQuestion(question);
    }
}
