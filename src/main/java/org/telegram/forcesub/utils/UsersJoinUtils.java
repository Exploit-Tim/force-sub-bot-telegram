package org.telegram.forcesub.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class UsersJoinUtils {

    public boolean isUserExist(Long chatId, Long channelId, TelegramClient telegramClient) throws Exception{
        GetChatMember getChatMember = GetChatMember.builder()
                .chatId(String.valueOf(channelId))
                .userId(chatId)
                .build();
        String execute = telegramClient.execute(getChatMember).getStatus();
        if (execute.equals("kicked") || execute.equals("left")) {
            return false;
        } else if (execute.equals("administrator")){
            return true;
        } else {
            return false;
        }

    }

}
