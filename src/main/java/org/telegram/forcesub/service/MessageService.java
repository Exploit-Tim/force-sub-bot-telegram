package org.telegram.forcesub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.forcesub.entity.Message;
import org.telegram.forcesub.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Transactional
    public String saveMessage(String messageId, String chatId) {
        try {
            String uuid = UUID.randomUUID().toString();
            Message message = Message.builder()
                    .uuid(uuid)
                    .messageId(messageId)
                    .chatId(chatId)
                    .build();
            
            // Log the message before saving
            log.debug("Attempting to save message: {}", message);
            
            Message savedMessage = messageRepository.save(message);
            
            // Verify the save was successful
            if (savedMessage == null || savedMessage.getUuid() == null) {
                log.error("Failed to save message - saved entity is null or missing UUID");
                return "Error saving message";
            }
            
            log.info("Message saved successfully - UUID: {}, MessageID: {}, ChatID: {}", 
                    savedMessage.getUuid(), savedMessage.getMessageId(), savedMessage.getChatId());
            
            return savedMessage.getUuid();
            
        } catch (Exception e) {
            log.error("Error saving message: ", e);
            return "Error saving message";
        }
    }

    @Transactional
    public String saveAllBatch(List<String> messageList, String chatId) {
        try {
            String uuid = UUID.randomUUID().toString();
            List<Message> messages = messageList.stream()
                    .map(messageId -> Message.builder()
                            .uuid(uuid)
                            .messageId(messageId)
                            .chatId(chatId)
                            .build())
                    .collect(Collectors.toList());

            log.debug("Attempting to save batch of {} messages", messages.size());
            
            List<Message> savedMessages = messageRepository.saveAll(messages);
            
            if (savedMessages.isEmpty()) {
                log.error("Failed to save batch messages - no messages were saved");
                return "Error saving messages";
            }

            log.info("Successfully saved {} messages with UUID: {}", savedMessages.size(), uuid);
            
            return uuid;
            
        } catch (Exception e) {
            log.error("Error saving batch messages: ", e);
            return "Error saving messages";
        }
    }

    public Optional<Message> getMessage(String uuid) {
        try {
            log.info("Searching for message with UUID: {}", uuid);
            return messageRepository.findByUuid(uuid);
        } catch (Exception e) {
            log.error("Error retrieving message with UUID {}: ", uuid, e);
            return Optional.empty();
        }
    }

public List<Long> getListOfMessageIdGreaterThan(String messageId, String lastMessageId) {
    try {
        log.info("Finding messages with ID greater than: {} and less than or equal to: {}", messageId, lastMessageId);
        List<Message> messages = messageRepository.findByMessageIdGreaterThanEqual(messageId);
        
        // Filter messages in memory to get the range we want
        List<Long> result = messages.stream()
                .map(Message::getMessageId)
                .filter(Objects::nonNull)
                .map(Long::parseLong)
                .filter(id -> id >= Long.parseLong(messageId) && id <= Long.parseLong(lastMessageId))
                .collect(Collectors.toList());
                
        log.info("Found {} messages", result.size());
        return result;
    } catch (Exception e) {
        log.error("Error getting messages greater than {}: ", messageId, e);
        return new ArrayList<>();
    }
}
    public Optional<Message> getAllMessage(String uuid) {
        try {
            log.info("Searching for message with UUID: {}", uuid);
            List<Message> messages = messageRepository.findAllByUuid(uuid);

            if (messages.isEmpty()) {
                log.info("No messages found for UUID: {}", uuid);
                return Optional.empty();
            }

            // Return the first message found
            Message firstMessage = messages.get(0);
            log.info("Found message with UUID: {}, MessageID: {}, ChatID: {}",
                    firstMessage.getUuid(), firstMessage.getMessageId(), firstMessage.getChatId());

            return Optional.of(firstMessage);
        } catch (Exception e) {
            log.error("Error retrieving message with UUID {}: ", uuid, e);
            return Optional.empty();
        }
    }
    public List<Message> getAllMessages(String uuid) {
        try {
            log.info("Searching for messages with UUID: {}", uuid);
            List<Message> messages = messageRepository.findAllByUuid(uuid);
            log.info("Found {} messages with UUID: {}", messages.size(), uuid);
            return messages;
        } catch (Exception e) {
            log.error("Error retrieving messages with UUID {}: ", uuid, e);
            return new ArrayList<>();
        }
    }
}