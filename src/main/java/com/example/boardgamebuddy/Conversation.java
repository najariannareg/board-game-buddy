package com.example.boardgamebuddy;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public record Conversation(@Id String conversationId, List<ConversationMessage> messages) {
}
