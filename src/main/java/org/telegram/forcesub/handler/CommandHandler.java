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

/**
 * Handles incoming Telegram bot commands by delegating them to appropriate command processors.
 * This class serves as the main command routing mechanism for the Telegram bot.
 */
@Component
public class CommandHandler {

    private static final Logger log = LoggerFactory.getLogger(CommandHandler.class);
    private final Map<String, CommandHandlerProcessor> commandMap;

    /**
     * Constructs a new CommandHandler with the given list of command processors.
     * Maps each processor to its corresponding command for quick lookup.
     *
     * @param commandHandlerProcessors List of available command processors
     */
    @Autowired
    CommandHandler(List<CommandHandlerProcessor> commandHandlerProcessors) {
        this.commandMap = commandHandlerProcessors.stream()
                .collect(Collectors.toMap(CommandHandlerProcessor::getCommand, c -> c));
    }

    /**
     * Asynchronously handles incoming Telegram updates by routing them to appropriate command processors.
     * Supports text messages, media messages (video, photo, document), and handles unsupported message types.
     *
     * @param update The incoming Telegram update
     * @param telegramClient The Telegram client instance for sending responses
     * @return CompletableFuture that completes when the command processing is done
     */
    @Async
    public CompletableFuture<Void> handle(Update update, TelegramClient telegramClient) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            log.info("Received message: {}", message.getText());
            CommandHandlerProcessor commandHandlerProcessor;

            if (message.hasText()) {
                // Extract command and find corresponding processor
                String commandKey = message.getText().split(" ")[0];
                commandHandlerProcessor = commandMap.getOrDefault(commandKey, commandMap.get("/"));
                commandHandlerProcessor.process(update, telegramClient);
                commandHandlerProcessor.process(message.getChatId(), message.getText(), telegramClient);
            } else if (message.hasVideo() || message.hasPhoto() || message.hasDocument()) {
                // Handle media messages with default processor
                commandHandlerProcessor = commandMap.getOrDefault("/", commandMap.get("/"));
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