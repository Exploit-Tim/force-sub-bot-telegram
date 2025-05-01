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

@Component
public interface CommandHandlerProcessor {
    Logger log = LoggerFactory.getLogger(CommandHandlerProcessor.class);

    String getCommand();
    String getDescription();
    default CompletableFuture<Void> process(long chatId, String text, TelegramClient telegramClient) {
        return CompletableFuture.completedFuture(null);
    }
    default CompletableFuture<Void> process(Update update, TelegramClient telegramClient) {
        return CompletableFuture.completedFuture(null);
    }
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
    default void replyMessageSendId(long chatId, long messageId, String text, TelegramClient telegramClient) {
        try {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .replyToMessageId((int) messageId)
                    .build();
            telegramClient.execute(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
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
