package org.telegram.forcesub.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.telegram.forcesub.utils.UsersJoinUtils;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class SubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);
    private final UserService userService;
    private final UsersJoinUtils usersJoinUtils;
    private final TelegramClient telegramClient;
    private final String botToken;
    private final RestTemplateBuilder restTemplateBuilder;

    public SubscriptionService(UserService userService,
                               UsersJoinUtils usersJoinUtils,
                               TelegramClient telegramClient,
                               @Value("${bot.token}") String botToken, RestTemplateBuilder restTemplateBuilder) {
        this.userService = userService;
        this.usersJoinUtils = usersJoinUtils;
        this.telegramClient = telegramClient;
        this.botToken = botToken;
        this.restTemplateBuilder = restTemplateBuilder;
    }

    public List<String> getUnsubcriptionUser(String chatId) {
        Set<String> allChannelIds = userService.getChannelIds();
        Set<String> unsubscribedChannelIds = new HashSet<>();

        log.info("Checking for unsubscription for chatId: {}", allChannelIds.size());

        for (String channel : allChannelIds) {
            try {
                boolean userExistInDatabase = userService.isUserExist(chatId, channel);
                log.info("Is Exist in Database [{}]: {}", channel, userExistInDatabase);

                if (!userExistInDatabase) {
                    userService.deleteUser(chatId, channel);
                    boolean userExistInChannel = usersJoinUtils.isUserExist(Long.parseLong(chatId), Long.parseLong(channel), telegramClient);
                    log.info("Is Exist in Channel [{}]: {}", channel, userExistInChannel);

                    if (!userExistInChannel) {
                        unsubscribedChannelIds.add(channel);
                    } else {
                        log.info("User {} still exists in channel {}, saving to DB", chatId, channel);
                        userService.saveUser(chatId, channel);
                    }
                }

                // Kalau userExistInDatabase == true => user sudah di DB, berarti tidak dianggap unsubscribe, jadi tidak masuk unsubscribedChannelIds

            } catch (Exception e) {
                throw new RuntimeException("Failed checking subscription for channel " + channel, e);
            }
        }
        log.info("Found {} unsubscription for chatId: {}", unsubscribedChannelIds.size(), chatId);
        return List.copyOf(unsubscribedChannelIds);
    }

    public String getInviteLink(String channelId) {
        String url = "https://api.telegram.org/bot" + botToken + "/exportChatInviteLink?chat_id=" + channelId;
        ResponseEntity<Map> response =
                restTemplateBuilder.build().getForEntity(url, Map.class);
        if (response.getStatusCode() == HttpStatus.OK && Boolean.TRUE.equals(response.getBody().get("ok"))) {
            return (String) response.getBody().get("result");
        } else {
            log.error("Failed to get invite link for channel {}: {}", channelId, response.getStatusCode());
            return null;
        }
    }


}
