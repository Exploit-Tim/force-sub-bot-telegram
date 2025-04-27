package org.telegram.forcesub.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
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
        CommandHandlerProcessor commandHandlerProcessor = commandMap.getOrDefault(update.getMessage().getText().split(" ")[0], commandMap.get("/"));
        commandHandlerProcessor.process(update, telegramClient);
        commandHandlerProcessor.process(update.getMessage().getChatId(), update.getMessage().getText(), telegramClient);
        return CompletableFuture.completedFuture(null);

    }
}
