package org.telegram.forcesub.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.forcesub.service.UserService;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class UsersJoinUtils {

    private static final Logger log = LoggerFactory.getLogger(UsersJoinUtils.class);
    private final UserService userService;

    public UsersJoinUtils(UserService userService1) {
        this.userService = userService1;
    }

    public boolean isExist(Long chatId, Long channelId, TelegramClient telegramClient) throws Exception{
        GetChatMember getChatMember = GetChatMember.builder()
                .chatId(String.valueOf(channelId))
                .userId(chatId)
                .build();
        String execute = telegramClient.execute(getChatMember).getStatus();
        if (execute.equals("kicked") || execute.equals("left")) {
            userService.deleteUser(chatId.toString(), channelId.toString());
            return false;
        } else if (execute.equals("administrator")){
            userService.deleteUser(chatId.toString(), channelId.toString());
            userService.saveUser(chatId.toString(), channelId.toString());
            return true;
        } else {
            userService.deleteUser(chatId.toString(), channelId.toString());
            return false;
        }
    }
    public boolean isUserExist(Long chatId, Long channelId, TelegramClient telegramClient) throws Exception{
        GetChatMember getChatMember = GetChatMember.builder()
                .chatId(String.valueOf(channelId))
                .userId(chatId)
                .build();
        String execute = telegramClient.execute(getChatMember).getStatus();
        log.info("Status is {}", execute );
        if (execute.equals("kicked") || execute.equals("left")) {
            userService.deleteUser(chatId.toString(), channelId.toString());
            return false;
        } else if (execute.equals("administrator") || execute.equals("member") || execute.equals("creator")){
            userService.deleteUser(chatId.toString(), channelId.toString());
            userService.saveUser(chatId.toString(), channelId.toString());
            return true;
        } else {
            userService.deleteUser(chatId.toString(), channelId.toString());
            return false;
        }
    }

}
