package org.telegram.forcesub.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Component
public class MapUtilsForEdit {

    private final String botUsername;
    private final String ownerUsername;

    public MapUtilsForEdit(@Value("${bot.username}") String botUsername,
                           @Value("${owner.username}") String ownerUsername) {
        this.botUsername = botUsername;
        this.ownerUsername = ownerUsername;
    }

    public Map<String, String> editMap(Update update) {

        Map<String, String> field = new HashMap<>();
        field.put("{username}", update.getMessage().getChat().getUserName());
        field.put("{userid}", update.getMessage().getChat().getId().toString());
        field.put("{firstname}", update.getMessage().getChat().getFirstName().trim());
        field.put("{lastname}", update.getMessage().getChat().getLastName().trim());
        field.put("{botusername}", botUsername);
        field.put("{ownerusername}", "https://t.me/" + ownerUsername);
        return field;

    }
}
