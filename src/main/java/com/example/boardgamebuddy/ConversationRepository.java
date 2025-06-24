package com.example.boardgamebuddy;

import org.springframework.data.repository.CrudRepository;

public interface ConversationRepository extends CrudRepository<Conversation, String> {
}
