package org.telegram.forcesub.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.forcesub.entity.MessageInformation;
import org.telegram.forcesub.entity.MessageText;
import org.telegram.forcesub.repository.MessageTextRepository;

@Service
public class MessageTextService {

    private final MessageTextRepository messageTextRepository;
    private final String startNotJoin;
    private final String startJoin;
    private final String startWelcome;

    public MessageTextService(
            MessageTextRepository messageTextRepository,
            @Value("${start.message.not.join}") String startNotJoin,
            @Value("${start.message.after.join}") String startJoin,
            @Value("${help.message}") String startWelcome
    ) {
        this.messageTextRepository = messageTextRepository;
        this.startNotJoin = startNotJoin;
        this.startJoin = startJoin;
        this.startWelcome = startWelcome;
    }

    public void saveMessageText(MessageInformation messageInformation, String text) {
        MessageText existingMessageText = messageTextRepository.getMessageTextsByMessageInformation(messageInformation);
        if (existingMessageText != null) {
            if (!existingMessageText.getText().equals(text)) {
                existingMessageText.setText(text);
                messageTextRepository.save(existingMessageText);
            }
        } else {
            messageTextRepository.save(MessageText.builder()
                    .messageInformation(messageInformation)
                    .text(text)
                    .build());
        }
    }
    public String getMessageText(MessageInformation messageInformation) {
        MessageText messageText = messageTextRepository.getMessageTextsByMessageInformation(messageInformation);
        if (messageText != null) {
            return messageText.getText();
        }
        return null;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void setUpMessageText() {
        messageTextRepository.deleteAll();

        saveMessageText(MessageInformation.START, startNotJoin);
        saveMessageText(MessageInformation.WELCOME, startWelcome);
        saveMessageText(MessageInformation.HELP, startJoin);
    }
}
