package org.telegram.forcesub.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.forcesub.service.MessageService;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class StartCommand implements CommandHandlerProcessor {

    private String database;

    private final MessageService messageService;

    public StartCommand(MessageService messageService, @Value("${data.message}") String database) {
        this.database = database;
        this.messageService = messageService;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getCommand() {
        return "/start";
    }

    @Override
    public CompletableFuture<Void> process(long chatId, String text, TelegramClient telegramClient) {
        return CompletableFuture.runAsync(() -> {
            if (text.equals("/start")){
                sendMessage(chatId, "Hello, I'm a bot. Type /help to see the list of commands.", telegramClient);
                return;
            }
            List<String> message = messageService.getMessage(text.replace("/start", "").trim());
            for (String s : message) {
                copyMessage(Long.parseLong(database), Integer.parseInt(s), chatId, telegramClient);
            }
        });
    }
}
