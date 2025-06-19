package com.example.boardgamebuddy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpringAiBoardGameService implements BoardGameService {

    private final ChatClient chatClient;
    private final RelevancyEvaluator evaluator;

    public SpringAiBoardGameService(ChatClient.Builder chatClientBuilder) {
        ChatOptions chatOptions = ChatOptions.builder()
                .model("gpt-4o-mini")
                .build();

        this.chatClient = chatClientBuilder
                .defaultOptions(chatOptions)
                .build();

        this.evaluator = new RelevancyEvaluator(chatClientBuilder);
    }

    @Override
    @Retryable(retryFor = AnswerNotRelevantException.class)
    public Answer askQuestion(Question question) {
        Answer answer = new Answer(chatClient.prompt()
                .user(question.question())
                .call()
                .content());

        evaluateRelevancy(question, answer);

        return answer;
    }

    @Recover
    public Answer recover(AnswerNotRelevantException e) {
        return new Answer("I'm sorry, I wasn't able to answer the question.");
    }

    private void evaluateRelevancy(Question question, Answer answer) {
        EvaluationRequest request = new EvaluationRequest(question.question(), List.of(), answer.answer());
        EvaluationResponse response = evaluator.evaluate(request);
        if (!response.isPass()) {
            throw new AnswerNotRelevantException(question, answer);
        }
    }
}
