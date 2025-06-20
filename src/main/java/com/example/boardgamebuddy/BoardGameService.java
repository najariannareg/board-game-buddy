package com.example.boardgamebuddy;

import reactor.core.publisher.Flux;

public interface BoardGameService {

    Answer askQuestion(Question question);

    Flux<String> streamQuestion(Question question);

}
