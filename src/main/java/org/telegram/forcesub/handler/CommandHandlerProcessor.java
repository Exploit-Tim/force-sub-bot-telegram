package org.telegram.forcesub.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for handling Telegram bot commands.
 * Provides common functionality for processing commands and sending messages.
 */
@Component
public interface CommandHandlerProcessor {
    Logger log = LoggerFactory.getLogger(CommandHandlerProcessor.class);

    /**
     * Gets the command string that this handler processes.
     *
     * @return The command string (e.g. "/start", "/help")
     */
    String getCommand();

    /**
     * Gets the description of what this command does.
     * You can set the description or not
     * @return The command description
     */
    String getDescription();

    /**
     * Processes a command with a given chat ID and text.
     *
     * @param chatId         The ID of the chat
     * @param text           The command text to process
     * @param telegramClient The telegram client instance
     * @return CompletableFuture that completes when processing is done
     */
    default CompletableFuture<Void> process(long chatId, String text, TelegramClient telegramClient) {
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Processes a Telegram update.
     *
     * @param update         The update object from Telegram
     * @param telegramClient The telegram client instance
     * @return CompletableFuture that completes when processing is done
     */
    default CompletableFuture<Void> process(Update update, TelegramClient telegramClient) {
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Sends a text message to a specified chat.
     * @param chatId The ID of the chat to send the message to
     * @param text The text content of the message
     * @param telegramClient The telegram client instance
     */
    default void sendMessage(Long chatId, String text, TelegramClient telegramClient) {
        try {
            telegramClient.execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .parseMode("Markdown")
                    .build());
        } catch (TelegramApiException e) {
            log.info(e.getMessage());
        }
    }

    /**
     * Copies a message from one chat to another.
     * @param fromChatId The source chats ID to copy from
     * @param messageId The ID of the message to copy
     * @param toChatId The destination chat ID
     * @param bot The telegram client instance
     */
    
    default void copyMessage(String fromChatId, String messageId, Long toChatId, TelegramClient bot) {
        CopyMessage copy = CopyMessage.builder()
                .fromChatId(fromChatId)
                .messageId(Integer.parseInt(messageId))
                .chatId(toChatId)
                .build();
        try {
            bot.execute(copy);
        } catch (TelegramApiException e) {
            log.info("Error copying message: {}", e.getMessage());
        }
    }

    /**
     * Sends a reply to a specific message.
     * @param chatId The ID of the chat
     * @param messageId The ID of the message to reply to
     * @param text The reply text content
     * @param telegramClient The telegram client instance
     */
    
    default void replyMessageSendId(long chatId, long messageId, String text, TelegramClient telegramClient) {
        try {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .replyToMessageId((int) messageId)
                    .parseMode("Markdown")
                    .build();
            telegramClient.execute(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a message with inline keyboard markup.
     * @param chatId The ID of the chat
     * @param text The text content of the message
     * @param markup The inline keyboard markup to attach
     * @param telegramClient The telegram client instance
     */
    
    default void sendMessageWithMarkup(Long chatId, String text, InlineKeyboardMarkup markup, TelegramClient telegramClient) {
        try {
            telegramClient.execute(SendMessage.builder()
                    .chatId(chatId)
                    .replyMarkup(markup)
                    .text(text)
                    .parseMode("Markdown")
                    .build());
        } catch (TelegramApiException e) {
            log.info(e.getMessage());
        }
    }


}
