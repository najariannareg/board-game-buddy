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

import java.util.List;

@Service
public class SpringAiBoardGameService implements BoardGameService {

    private final ChatClient chatClient;
    private final RelevancyEvaluator evaluator;

    @Value("classpath:/prompttemplates/questionPromptTemplate.st")
    Resource questionPromptTemplate;

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
        String answerText = chatClient.prompt()
                .user(spec -> spec
                        .text(questionPromptTemplate)
                        .param("gameTitle", question.gameTitle())
                        .param("question", question.question()))
                .call()
                .content();

        Answer answer = new Answer(question.gameTitle(), answerText);
        evaluateRelevancy(question, answer);

        return answer;
    }

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
