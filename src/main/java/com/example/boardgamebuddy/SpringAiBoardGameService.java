package com.example.boardgamebuddy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class SpringAiBoardGameService implements BoardGameService {

    private final ChatClient chatClient;
    private final RelevancyEvaluator evaluator;
    private final GameRulesService gameRulesService;

    @Value("classpath:/prompttemplates/systemPromptTemplate.st")
    Resource promptTemplate;

    public SpringAiBoardGameService(ChatClient.Builder chatClientBuilder, GameRulesService gameRulesService) {
        ChatOptions chatOptions = ChatOptions.builder()
                .model("gpt-4o-mini")
                .build();

        this.chatClient = chatClientBuilder
                .defaultOptions(chatOptions)
                .build();

        this.evaluator = new RelevancyEvaluator(chatClientBuilder);
        this.gameRulesService = gameRulesService;
    }

    @Override
    @Retryable(retryFor = AnswerNotRelevantException.class)
    public Answer askQuestion(Question question) {
        String rules = gameRulesService.getRulesFor(question.gameTitle());

        return chatClient.prompt()
                .system(systemSpec -> systemSpec
                        .text(promptTemplate)
                        .param("gameTitle", question.gameTitle())
                        .param("rules", rules))
                .user(question.question())
                .call()
                .entity(Answer.class);
    }

    @Override
    public Flux<String> streamQuestion(Question question) {
        String rules = gameRulesService.getRulesFor(question.gameTitle());

        return chatClient.prompt()
                .system(systemSpec -> systemSpec
                        .text(promptTemplate)
                        .param("gameTitle", question.gameTitle())
                        .param("rules", rules))
                .user(question.question())
                .stream()
                .content();
    }


    // Evaluation Utils
    @Recover
    public Answer recover(AnswerNotRelevantException e) {
        return new Answer(null, "I'm sorry, I wasn't able to answer the question.");
    }

    private void evaluateRelevancy(Question question, Answer answer) {
        EvaluationRequest request = new EvaluationRequest(question.question(), List.of(), answer.answer());
        EvaluationResponse response = evaluator.evaluate(request);
        if (!response.isPass()) {
            throw new AnswerNotRelevantException(question, answer);
        }
    }
}
