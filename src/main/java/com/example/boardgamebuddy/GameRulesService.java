package com.example.boardgamebuddy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;

@Service
public class GameRulesService {

    private static final Logger logger = LoggerFactory.getLogger(GameRulesService.class);

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
}
