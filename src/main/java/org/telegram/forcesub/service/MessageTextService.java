package org.telegram.forcesub.service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.forcesub.entity.MessageInformation;
import org.telegram.forcesub.entity.MessageText;
import org.telegram.forcesub.repository.MessageTextRepository;


@Service
public class MessageTextService {

    private final MessageTextRepository messageTextRepository;

    public MessageTextService(MessageTextRepository messageTextRepository) {
        this.messageTextRepository = messageTextRepository;
    }
    public void saveMessageText(MessageInformation messageInformation, String text) {
        MessageText messageText = messageTextRepository.getMessageTextsByMessageInformation(messageInformation);
        if (!messageText.getText().equals(text)) {
            messageText.setText(text);
            messageTextRepository.save(messageText);
            return;
        }
        messageTextRepository.save(MessageText.builder()
                        .text(text)
                        .messageInformation(messageInformation)
                .build());

    }

    @EventListener(ApplicationReadyEvent.class)
    public void setUpMessageText() {
        MessageText welcome = MessageText.builder()
                .messageInformation(MessageInformation.WELCOME)
                .text("""
                        Selamat datang {firstname} di Bot untuk mengirimkan pesan
                        yang dibagikan oleh pemilik.
                        """)
                .build();
        messageTextRepository.save(welcome);
        MessageText help = MessageText.builder()
                .messageInformation(MessageInformation.HELP)
                .text("""
                        Selamat datang {firstname} di Bot untuk mengirimkan pesan
                        yang dibagikan oleh pemilik.
                        """)
                .build();
        messageTextRepository.save(help);
        MessageText start = MessageText.builder()
                .messageInformation(MessageInformation.START)
                .text("""
                        Selamat datang {firstname} di Bot untuk mengirimkan pesan
                        yang dibagikan oleh pemilik.
                        """)
                .build();
        messageTextRepository.save(start);
    }
}
