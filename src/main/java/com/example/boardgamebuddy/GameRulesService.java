package com.example.boardgamebuddy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameRulesService {

    private static final Logger logger = LoggerFactory.getLogger(GameRulesService.class);

    private final VectorStore vectorStore;

    public GameRulesService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public String getRulesFor(String gameTitle) {
        try {
            String filename = String.format("classpath:/gamerules/%s.txt",
                    gameTitle.toLowerCase().replace(" ", "_"));
            return new DefaultResourceLoader().getResource(filename).getContentAsString(Charset.defaultCharset());
        } catch (IOException e) {
            logger.info("No rules found for game: {}", gameTitle);
            return "";
        }
    }

    public String getRulesFor(Question question) {
        SearchRequest searchRequest = SearchRequest
                .builder()
                .query(question.gameTitle())
//                .similarityThreshold(0.5f)
//                .topK(6)
//                .filterExpression(
//                        new FilterExpressionBuilder()
//                                .eq("gameTitle", normalizeGameTitle(question.gameTitle())).build())
                .build();

        List<Document> similarDocs = vectorStore.similaritySearch(searchRequest);

        if (similarDocs == null || similarDocs.isEmpty()) {
            return "The rules for " + question.gameTitle() + " are not available.";
        }

        return similarDocs.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));
    }

    private String normalizeGameTitle(String gameTitle) {
        return gameTitle.toLowerCase().replace(" ", "_");
    }
}
