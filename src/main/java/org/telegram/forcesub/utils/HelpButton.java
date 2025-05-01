package org.telegram.forcesub.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

@Component
public class HelpButton {

    public InlineKeyboardMarkup helpButton(String botOwner) {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .url("https://t.me/" + botOwner)
                        .text("Contact Bot Owner")
                        .build())))
                .build();
    }
}
