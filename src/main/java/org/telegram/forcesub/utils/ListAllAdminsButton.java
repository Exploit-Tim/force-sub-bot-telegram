package org.telegram.forcesub.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ListAllAdminsButton {

    public InlineKeyboardMarkup adminsButton(List<String> adminsId, String action) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        InlineKeyboardRow currentRow = new InlineKeyboardRow();

        for (int i = 0; i < adminsId.size(); i++) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(adminsId.get(i))
                    .callbackData(action + "_" + adminsId.get(i))
                    .build();
            currentRow.add(button);

            if (currentRow.size() == 2 || i == adminsId.size() - 1) {
                rows.add(currentRow);
                currentRow = new InlineKeyboardRow();
            }
        }

        return new InlineKeyboardMarkup(rows);
    }
}
