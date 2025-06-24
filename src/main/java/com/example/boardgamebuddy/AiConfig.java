package com.example.boardgamebuddy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

//    @Bean
//    ChatClient chatClient(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
//        return chatClientBuilder
//                .defaultOptions(ChatOptions.builder()
//                        .model("gpt-4o-mini")
//                        .build())
//                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
//                .build();
//    }

//    @Bean
//    ChatClient chatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {
//        return chatClientBuilder
//                .defaultOptions(ChatOptions.builder()
//                        .model("gpt-4o-mini")
//                        .build())
//                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
//                .build();
//    }
//
//    @Bean
//    ChatMemory chatMemory(ConversationRepository repository) {
//        return new MongoChatMemory(repository);
//    }

    @Bean
    ChatClient chatClient(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        return chatClientBuilder
                .defaultOptions(ChatOptions.builder()
                        .model("gpt-4o-mini")
                        .build())
                .defaultAdvisors(VectorStoreChatMemoryAdvisor.builder(vectorStore).build())
                .build();
    }

}
