package org.telegram.forcesub.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.forcesub.service.MessageService;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.concurrent.CompletableFuture;

@Component
public class GenerateIdMessage implements CommandHandlerProcessor {

    private final String database;
    private final MessageService messageService;
    private final String botUsername;

    public GenerateIdMessage(@Value("${data.message}") String database, MessageService messageService, @Value("${bot.username}") String botUsername) {
        this.database = database;
        this.messageService = messageService;
        this.botUsername = botUsername;
    }


    @Override
    public String getCommand() {
        return "/";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public CompletableFuture<Void> process(Update update, TelegramClient telegramClient) {
        log.info("Get Message");
        return CompletableFuture.runAsync(() -> {
            if (update.getMessage().getChatId().equals(Long.parseLong(database))){
                String message = messageService.saveMessage(update.getMessage().getMessageId().toString(), update.getMessage().getChatId().toString());
                replyMessageSendId(update.getMessage().getChatId(), update.getMessage().getMessageId() ,String.format("https://t.me/%s?start=%s", botUsername, message), telegramClient);
            }
        });
    }
}
