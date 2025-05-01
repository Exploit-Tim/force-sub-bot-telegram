package org.telegram.forcesub.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.forcesub.entity.MessageInformation;
import org.telegram.forcesub.service.MessageTextService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class MappingText {

    private static final Logger log = LoggerFactory.getLogger(MappingText.class);
    private final MessageTextService messageTextService;
    private final String botUsername;
    private final String ownerUsername;

    public MappingText(MessageTextService messageTextService,
                       @Value("${bot.username}") String  botUsername,
                       @Value("${owner.username}") String ownerUsername) {
        this.messageTextService = messageTextService;
        this.botUsername = botUsername;
        this.ownerUsername = ownerUsername;
    }

    public String replacePlaceholders(MessageInformation messageInfo, Update update) {
        Map<String, String> fields = new HashMap<>();

        var chat = update.getMessage().getChat();
        fields.put("{username}", Optional.ofNullable(chat.getUserName()).orElse(""));
        fields.put("{userid}", chat.getId().toString());
        fields.put("{firstname}", Optional.ofNullable(chat.getFirstName()).orElse("").trim());
        fields.put("{lastname}", Optional.ofNullable(chat.getLastName()).orElse("").trim());
        fields.put("{botusername}", botUsername);
        fields.put("{ownerusername}", "https://t.me/" + ownerUsername);

        String template = messageTextService.getMessageText(messageInfo);
        log.info("Template: {}", template);

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            template = template.replace(entry.getKey(), entry.getValue());
        }

        return template;
    }
}
