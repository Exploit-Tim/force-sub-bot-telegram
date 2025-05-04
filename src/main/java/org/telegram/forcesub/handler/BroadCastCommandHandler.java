package org.telegram.forcesub.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.forcesub.service.AdminService;
import org.telegram.forcesub.service.UserService;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Handles the /broadcast command to send a message to all subscribers.
 * This command is restricted to admins only.
 */

@Slf4j
@Component
public class BroadCastCommandHandler implements CommandHandlerProcessor {

    /**
     * The delay between sending messages to different users to avoid hitting rate limits.
     * Value is in milliseconds.
     */
    private static final Duration MESSAGE_THROTTLE_DELAY = Duration.ofMillis(500);
    
    private final UserService userService;
    private final AdminService adminService;

    public BroadCastCommandHandler(UserService userService, AdminService adminService) {
        this.userService = userService;
        this.adminService = adminService;
    }

    @Override
    public String getCommand() {
        return "/broadcast";
    }

    @Override
    public String getDescription() {
        return "Broadcast a message to all subscribers";
    }

    /**
     *
     * Admins Command To Broadcast To All users,
     * This Method Can Broadcast To All users, not Admins, or Owner.
     *
     * @param chatId         The ID of the chat
     * @param text           The command text to process
     * @param telegramClient The telegram client instance
     */

    @Override
    @Async
    public CompletableFuture<Void> process(long chatId, String text, TelegramClient telegramClient) {
        return CompletableFuture.runAsync(() -> {
            if (adminService.isAdminExist(String.valueOf(chatId))) {
                // If User is Not Admin, Cannot Send BroadCast
                sendMessage(chatId, "You Not An Admin", telegramClient);
            } else {
                if (text.startsWith("/broadcast")) {

                    // Extract the message from the command
                    String message = text.substring(10).trim();
                    if (message.isEmpty()) {
                        // If Message is Empty, Send to the sender to set A Message
                        sendMessage(chatId, "Please provide a message to broadcast.", telegramClient);
                    } else {
                        // Get All Users In Database
                        Set<String> subscribers = userService.getAllUsers();
                        log.info("Broadcasting message to {} subscribers", subscribers.size());
                        sendMessage(chatId, "Starting broadcast to " + subscribers.size() + " users...", telegramClient);

                        int successCount = 0;
                        for (String subscriber : subscribers) {
                            try {
                                applyMessageThrottle();
                                sendMessage(Long.parseLong(subscriber), "Broadcasting message: " + message, telegramClient);
                                successCount++;
                            } catch (Exception e) {
                                log.error("Failed to send broadcast to user {}: {}", subscriber, e.getMessage());
                            }
                        }

                        log.info("Broadcast completed: {} messages sent successfully", successCount);
                        sendMessage(chatId, "Broadcast completed: " + successCount + " of " +
                                subscribers.size() + " messages delivered successfully.", telegramClient);
                    }
                } else {
                    sendMessage(chatId, "Invalid command. Use /broadcast <message>.", telegramClient);
                }
            }
        });
    }
    
    /**
     * Applies a delay between messages to prevent hitting Telegram API rate limits.
     * This throttling is essential for successful delivery of messages to large audiences.
     */
    private void applyMessageThrottle() {
        try {
            TimeUnit.MILLISECONDS.sleep(MESSAGE_THROTTLE_DELAY.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Message throttling interrupted", e);
        }
    }
}