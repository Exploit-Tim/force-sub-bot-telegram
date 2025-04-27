package org.telegram.forcesub.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.forcesub.entity.Message;
import org.telegram.forcesub.repository.MessageRepository;

import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }


    public List<String> getMessage(String uuid) {
        return messageRepository.findByUuid(uuid).stream().toList();
    }

    @Transactional
    public String saveMessage(List<String> messageId) {
        String uuid = UUID.randomUUID().toString();
        Message message = Message.builder()
                .uuid(uuid)
                .messageId(messageId)
                .build();
        try {
            messageRepository.save(message);
            return uuid;
        } catch (Exception e) {
            return "Error saving message";
        }
    }
    public Long countAllMessage() {
        return messageRepository.count();
    }
}
