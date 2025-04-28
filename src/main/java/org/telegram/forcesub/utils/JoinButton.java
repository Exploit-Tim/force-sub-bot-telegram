package org.telegram.forcesub.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class JoinButton {

    public InlineKeyboardMarkup joinButton(List<String> channelIds) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        InlineKeyboardRow currentRow = new InlineKeyboardRow();

        for (int i = 0; i < channelIds.size(); i++) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text("Join Channel")
                    .url(channelIds.get(i))
                    .build();
            currentRow.add(button);

            if (currentRow.size() == 2 || i == channelIds.size() - 1) {
                rows.add(currentRow);
                currentRow = new InlineKeyboardRow();
            }
        }

        return new InlineKeyboardMarkup(rows);
    }

}
