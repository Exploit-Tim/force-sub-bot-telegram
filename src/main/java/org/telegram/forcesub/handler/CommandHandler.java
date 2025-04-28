package org.telegram.forcesub.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class CommandHandler {

    private static final Logger log = LoggerFactory.getLogger(CommandHandler.class);
    private final Map<String, CommandHandlerProcessor> commandMap;
    @Autowired
    CommandHandler(List<CommandHandlerProcessor> commandHandlerProcessors) {
        this.commandMap = commandHandlerProcessors.stream().collect(Collectors.toMap(CommandHandlerProcessor::getCommand, c -> c));
    }

    @Async
    public CompletableFuture<Void> handle(Update update, TelegramClient telegramClient) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            log.info("Received message: {}", message.getText());
            CommandHandlerProcessor commandHandlerProcessor;

            if (message.hasText()) {
                String commandKey = message.getText().split(" ")[0];
                commandHandlerProcessor = commandMap.getOrDefault(commandKey, commandMap.get("/"));
                commandHandlerProcessor.process(update, telegramClient);
                commandHandlerProcessor.process(message.getChatId(), message.getText(), telegramClient);
            } else if (message.hasVideo() || message.hasPhoto() || message.hasDocument()) {
                commandHandlerProcessor = commandMap.getOrDefault("/", commandMap.get("/"));
                commandHandlerProcessor.process(update, telegramClient);
            } else if (message.hasLocation()) {
                // Kalau kamu juga mau handle location (goto mungkin maksudnya)
                commandHandlerProcessor = commandMap.getOrDefault("/location", commandMap.get("/"));
                commandHandlerProcessor.process(update, telegramClient);
            } else {
                log.info("Unsupported message type: {}", message);
            }
        } else {
            log.info("Unsupported update type: {}", update);
        }

        return CompletableFuture.completedFuture(null);
    }

}
