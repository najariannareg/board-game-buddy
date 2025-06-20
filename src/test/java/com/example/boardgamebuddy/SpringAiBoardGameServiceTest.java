package com.example.boardgamebuddy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringAiBoardGameServiceTest {

    @Autowired
    private BoardGameService boardGameService;

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    private RelevancyEvaluator relevancyEvaluator;

    private FactCheckingEvaluator factCheckingEvaluator;

    @BeforeEach
    void setUp() {
        this.relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);
        this.factCheckingEvaluator = new FactCheckingEvaluator(chatClientBuilder);
    }

    @Test
    void evaluateRelevancy() {
        String gameTitle = "checkers";
        String userText = "How many pieces are there?";
        Question question = new Question(gameTitle, userText);
        Answer answer = boardGameService.askQuestion(question);

        EvaluationRequest evaluationRequest = new EvaluationRequest(userText, answer.answer());
        EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

        Assertions.assertTrue(evaluationResponse.isPass());
    }

    @Test
    void evaluateFactualAccuracy() {
        String gameTitle = "checkers";
        String userText = "How many pieces are there?";
        Question question = new Question(gameTitle, userText);
        Answer answer = boardGameService.askQuestion(question);

        EvaluationRequest evaluationRequest = new EvaluationRequest(userText, answer.answer());
        EvaluationResponse evaluationResponse = factCheckingEvaluator.evaluate(evaluationRequest);

        Assertions.assertTrue(evaluationResponse.isPass());
    }
}