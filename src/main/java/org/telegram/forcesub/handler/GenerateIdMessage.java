package org.telegram.forcesub.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.forcesub.utils.InlineKeyboardForDatabase;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class GenerateIdMessage implements CommandHandlerProcessor {

    private final String database;
    private final InlineKeyboardForDatabase inlineKeyboardForDatabase;

    public GenerateIdMessage(@Value("${data.message}") String database, InlineKeyboardForDatabase inlineKeyboardForDatabase) {
        this.database = database;
        this.inlineKeyboardForDatabase = inlineKeyboardForDatabase;
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
                replyMessageSendId(update.getMessage().getChatId(), update.getMessage().getMessageId(), "DONE", inlineKeyboardForDatabase.getInlineKeyboardForDatabase(List.of(String.valueOf(update.getMessage().getMessageId()))), telegramClient);
            }
        });
    }
}
