package org.telegram.forcesub.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.forcesub.entity.Message;
import org.telegram.forcesub.service.AdminService;
import org.telegram.forcesub.service.MessageService;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * The CommandHandler to see the messageId
 * Generate MessageID if they are not UUID
 * Create Batch Processing MessageId if they send UUID of the messageId
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class GenerateIdMessage implements CommandHandlerProcessor {
    /**
     * Regex pattern for UUID validation.
     */
    private static final Pattern UUID_PATTERN = Pattern.compile(
            "\\b[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}\\b");
    private static final String ERROR_MESSAGES_NOT_FOUND = "Error: Salah satu messageId tidak ditemukan";
    private static final String ERROR_NO_MESSAGES_IN_RANGE = "Tidak Ada";
    private static final String ERROR_PROCESSING_MESSAGES = "Error Prosess Pesan";
    private static final String NEXT_MESSAGE_PROMPT = "\n\nKirim MessageId terakhir";
    private final MessageService messageService;
    private final Map<String, String> messageMap = new HashMap<>();
    private final AdminService adminService;

    @Value("${data.message}")
    private String database;

    @Value("${bot.username}")
    private String botUsername;

    @Value("${owner.username}")
    private String ownerUsername;

    @Override
    public String getCommand() {
        return "/";
    }

    @Override
    public String getDescription() {
        return "";
    }

    /**
     *
     * The Main Method to handle message if they are not Command
     * Generate UUID tho messageId and send The Link To File
     * @param update         The update object from Telegram
     * @param telegramClient The telegram client instance
     */

    @Override
    @Async
    public CompletableFuture<Void> process(Update update, TelegramClient telegramClient) {
        return CompletableFuture.runAsync(() -> processMessage(update, telegramClient));
    }

    /**
     * Process Message
     * @param update
     * @param telegramClient
     */

    private void processMessage(Update update, TelegramClient telegramClient) {

        if (adminService.isAdminExist(String.valueOf(update.getMessage().getChatId()))) {
            sendMessage(update.getMessage().getChatId(), "Anda tidak memiliki akses", telegramClient);
            return;
        }
        if (!isValidUpdate(update)) {
            return;
        }

        String messageText = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        if (isAuthorizedNonUuidMessage(chatId, messageText)) {
            handleAuthorizedUserMessage(update, telegramClient);
            return;
        }

        processUuidMessage(update, telegramClient);
    }

    /**
     * This Method to handle null message, include joined User, Left user, Reaction, and more
     * @param update
     * @return
     */

    private boolean isValidUpdate(Update update) {
        if (update.getMessage() == null) {
            log.warn("Update message is null");
            return false;
        }
        return true;
    }

    /**
     * Detect UUID Message, if They Are UUID, bot process Batch Message
     * @param text
     * @return boolean
     */

    private boolean isUuidMessage(String text) {
        return UUID_PATTERN.matcher(text).matches();
    }

    /**
     * Send the message Link if User is Authorized, as Owner or Admin
     * @param update
     * @param telegramClient
     */

    private void handleAuthorizedUserMessage(Update update, TelegramClient telegramClient) {
        String messageId = update.getMessage().getMessageId().toString();
        String chatId = update.getMessage().getChatId().toString();
        String generatedId = messageService.saveMessage(messageId, chatId);
        sendGeneratedLink(update, telegramClient, generatedId);
    }

    private boolean isAuthorizedNonUuidMessage(Long chatId, String messageText) {
        boolean isAuthorized = chatId != null &&
                (chatId.equals(Long.parseLong(database)) ||
                        chatId.equals(Long.parseLong(ownerUsername)) ||
                        adminService.countAllAdmin().contains(chatId.toString()));
        boolean isNotUuid = messageText == null || !isUuidMessage(messageText);
        return isAuthorized && isNotUuid;
    }

    private void processUuidMessage(Update update, TelegramClient telegramClient) {
        String messageText = update.getMessage().getText();
        if (messageText != null && isUuidMessage(messageText)) {
            String chatIdStr = update.getMessage().getChatId().toString();
            if (messageMap.containsKey(chatIdStr)) {
                handleSecondUuidMessage(messageMap.get(chatIdStr), update, telegramClient);
            } else {
                handleFirstUuidMessage(chatIdStr, messageText, update, telegramClient);
            }
        }
    }

    private void handleFirstUuidMessage(String chatIdStr, String messageText, Update update, TelegramClient telegramClient) {
        if (messageText == null || messageText.trim().isEmpty()) {
            log.error("Invalid message text received");
            return;
        }
        messageMap.put(chatIdStr, messageText);
        sendMessage(update.getMessage().getChatId(),
                "Message ID : " + messageText + NEXT_MESSAGE_PROMPT,
                telegramClient);
    }

    private void handleSecondUuidMessage(String firstUuid, Update update, TelegramClient telegramClient) {
        MessagePair messagePair = getMessagePair(firstUuid, update.getMessage().getText());
        if (!messagePair.isValid()) {
            sendMessage(update.getMessage().getChatId(), ERROR_MESSAGES_NOT_FOUND, telegramClient);
            return;
        }

        List<Long> messages = getMessagesInRange(messagePair);
        if (messages.isEmpty()) {
            sendMessage(update.getMessage().getChatId(), ERROR_NO_MESSAGES_IN_RANGE, telegramClient);
            return;
        }

        processMessageBatch(messages, update, telegramClient);
    }

    private MessagePair getMessagePair(String firstUuid, String secondUuid) {
        Optional<Message> firstMessage = messageService.getMessage(firstUuid);
        Optional<Message> secondMessage = messageService.getMessage(secondUuid);
        return new MessagePair(firstMessage, secondMessage);
    }

    private List<Long> getMessagesInRange(MessagePair messagePair) {
        String startId = messagePair.getStartMessageId();
        String endId = messagePair.getEndMessageId();
        return messageService.getListOfMessageIdGreaterThan(startId, endId);
    }

    private void processMessageBatch(List<Long> messages, Update update, TelegramClient telegramClient) {
        List<String> messageStrings = messages.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());

        String generatedId = messageService.saveAllBatch(messageStrings, database);
        if (!"Error saving messages".equals(generatedId)) {
            sendGeneratedLink(update, telegramClient, generatedId);
            messageMap.remove(update.getMessage().getChatId().toString());
        } else {
            sendMessage(update.getMessage().getChatId(), ERROR_PROCESSING_MESSAGES, telegramClient);
        }
    }

    private void sendGeneratedLink(Update update, TelegramClient telegramClient, String generatedId) {
        String formattedBotUsername = botUsername.replace("_", "\\_");
        String messageLink = String.format("https://t.me/%s?start=%s\n\nMessageId : `%s`",
                formattedBotUsername, generatedId, generatedId);
        replyMessageSendId(update.getMessage().getChatId(),
                update.getMessage().getMessageId(),
                messageLink,
                telegramClient);
    }

    private record MessagePair(Optional<Message> first, Optional<Message> second) {

        boolean isValid() {
                return first.isPresent() && second.isPresent();
            }

            String getStartMessageId() {
                if (!isValid()) {
                    throw new IllegalStateException("Cannot get message IDs from invalid MessagePair");
                }

                String firstId = first.get().getMessageId();
                String secondId = second.get().getMessageId();
                return Long.parseLong(firstId) <= Long.parseLong(secondId) ? firstId : secondId;
            }

            String getEndMessageId() {
                if (!isValid()) {
                    throw new IllegalStateException("Cannot get message IDs from invalid MessagePair");
                }

                String firstId = first.get().getMessageId();
                String secondId = second.get().getMessageId();
                return Long.parseLong(firstId) > Long.parseLong(secondId) ? firstId : secondId;
            }
        }
}