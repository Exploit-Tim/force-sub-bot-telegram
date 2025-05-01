package org.telegram.forcesub.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.forcesub.entity.MessageInformation;
import org.telegram.forcesub.utils.HelpButton;
import org.telegram.forcesub.utils.MappingText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.concurrent.CompletableFuture;

@Component
public class HelpCommand implements CommandHandlerProcessor {
    private final HelpButton helpButton;
    private final String botOwner;
    private final MappingText mappingText;

    public HelpCommand(HelpButton helpButton,
                       @Value("${owner.username}") String botOwner, MappingText mappingText) {
        this.helpButton = helpButton;
        this.botOwner = botOwner;
        this.mappingText = mappingText;
    }

    @Override
    public String getCommand() {
        return "/help";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public CompletableFuture<Void> process(Update update, TelegramClient telegramClient) {
        return CompletableFuture.runAsync(() -> sendMessageWithMarkup(update.getMessage().getChatId(),
                mappingText.replacePlaceholders(MessageInformation.ABOUT, update),
                helpButton.helpButton(botOwner),
                telegramClient));
    }
}
