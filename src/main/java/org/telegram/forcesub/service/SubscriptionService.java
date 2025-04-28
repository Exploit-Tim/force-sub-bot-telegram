package org.telegram.forcesub.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.forcesub.utils.UsersJoinUtils;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);
    private final UserService userService;
    private final UsersJoinUtils usersJoinUtils;
    private final TelegramClient telegramClient;

    public SubscriptionService(UserService userService, UsersJoinUtils usersJoinUtils, TelegramClient telegramClient) {
        this.userService = userService;
        this.usersJoinUtils = usersJoinUtils;
        this.telegramClient = telegramClient;
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


}
