package com.example.boardgamebuddy;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoChatMemory implements ChatMemory {

    private final ConversationRepository conversationRepository;

    public MongoChatMemory(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        List<ConversationMessage> conversationMessages = messages.stream()
                .map(message -> new ConversationMessage(
                        message.getMessageType().getValue(), message.getText()
                )).toList();

        conversationRepository.findById(conversationId)
                .ifPresentOrElse(conversation -> {
                    List<ConversationMessage> existingMessages = conversation.messages();
                    existingMessages.addAll(conversationMessages);
                    conversationRepository.save(new Conversation(conversationId, existingMessages));
                },
                () -> conversationRepository.save(new Conversation(conversationId, conversationMessages)));
    }

    @Override
    public List<Message> get(String conversationId) {
        return conversationRepository.findById(conversationId)
                .map(conversation -> {
                    return conversation.messages().stream()
                            .map(conversationMessage -> {
                                String messageType = conversationMessage.messageType();
                                Message message = messageType.equals(MessageType.USER.getValue()) ?
                                        new UserMessage(conversationMessage.content()) :
                                        new AssistantMessage(conversationMessage.content());
                                return message;
                            }).toList();
                }).orElse(List.of());
    }

    @Override
    public void clear(String conversationId) {
        conversationRepository.deleteById(conversationId);
    }
}
