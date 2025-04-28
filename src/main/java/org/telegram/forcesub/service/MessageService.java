package org.telegram.forcesub.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.forcesub.entity.Message;
import org.telegram.forcesub.repository.MessageRepository;
import java.util.UUID;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }


    public Message getMessage(String uuid) {
        return messageRepository.findByUuid(uuid);
    }

    @Transactional
    public String saveMessage(String messageId, String chatId) {
        String uuid = UUID.randomUUID().toString();
        Message message = Message.builder()
                .uuid(uuid)
                .messageId(messageId)
                .chatId(chatId)
                .build();
        try {
            messageRepository.save(message);
            return uuid;
        } catch (Exception e) {
            return "Error saving message";
        }
    }
}
