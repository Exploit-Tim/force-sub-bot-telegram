package org.telegram.forcesub.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.forcesub.service.MessageService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.CopyTextButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class InlineKeyboardForDatabase {


    private static final Logger log = LoggerFactory.getLogger(InlineKeyboardForDatabase.class);
    private final MessageService messageService;

    private final String botUsername;

    public InlineKeyboardForDatabase(MessageService messageService, @Value("${bot.username}") String botUsername) {
        this.messageService = messageService;
        this.botUsername = botUsername;
    }

    public InlineKeyboardMarkup getInlineKeyboardForDatabase(List<String> message) {

        String s = messageService.saveMessage(message);
        log.info("Saved message: {}", s);

        CopyTextButton copyTextButton = new CopyTextButton(String.format("https://t.me/%s?start=%s", botUsername, s) );
        List<InlineKeyboardRow> rows = new ArrayList<>();

        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .copyText(copyTextButton)
                .build();
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(button);
        rows.add(row);
        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}
