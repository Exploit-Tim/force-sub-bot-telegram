
package org.telegram.forcesub.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.forcesub.utils.UsersJoinUtils;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import java.util.ArrayList;
import java.util.List;

@Component
public class PreRun {

    private final UsersJoinUtils usersJoinUtils;
    private final TelegramClient telegramClient;
    private final Long botId;
    private final String database;

    private static final Logger log = LoggerFactory.getLogger(PreRun.class);
    private final UserService userService;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY = 5000; // 5 seconds

    public PreRun(UserService userService, @Value("${bot.id}") String botUsername,
                  UsersJoinUtils usersJoinUtils, TelegramClient telegramClient,
                  @Value("${data.message}") String database) {
        this.userService = userService;
        this.botId = Long.parseLong(botUsername);
        this.usersJoinUtils = usersJoinUtils;
        this.telegramClient = telegramClient;
        this.database = database;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void preRun() {
        log.info("Starting Bot Configuration...");
        List<String> missingChannels = new ArrayList<>();
        List<String> channelIds = new ArrayList<>(userService.getChannelIds().stream().toList());
        channelIds.add(database);

        for (String id : channelIds) {
            if (!verifyChannelMembership(id, MAX_RETRIES)) {
                missingChannels.add(id);
            }
        }

        if (!missingChannels.isEmpty()) {
            log.error("Bot is not a member of the following channels: {}", missingChannels);
            log.error("Please ensure the following:");
            log.error("1. The bot is added to these channels as an administrator");
            log.error("2. The channel IDs are correct in your configuration");
            log.error("3. The bot has sufficient permissions in the channels");
            throw new RuntimeException("Bot membership verification failed for channels: " + missingChannels);
        }

        log.info("Bot configuration completed successfully");
    }

    private boolean verifyChannelMembership(String channelId, int retriesLeft) {
        try {
            if (usersJoinUtils.isUserExist(botId, Long.parseLong(channelId), telegramClient)) {
                log.info("Bot {} successfully verified in channel {}", botId, channelId);
                return true;
            }
            log.warn("Bot {} is not a member of channel {}", botId, channelId);
        } catch (Exception e) {
            log.error("Error checking bot membership for channel {}: {}", channelId, e.getMessage());
            if (retriesLeft > 0) {
                log.info("Retrying verification for channel {} ({} attempts left)", channelId, retriesLeft);
                try {
                    Thread.sleep(RETRY_DELAY);
                    return verifyChannelMembership(channelId, retriesLeft - 1);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return false;
    }
}