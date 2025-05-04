package org.telegram.forcesub.handler;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.forcesub.service.AdminService;
import org.telegram.forcesub.utils.ListAllAdminsButton;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Component
public class RemoveAdminCommandHandler implements CommandHandlerProcessor {
    private final AdminService adminService;
    private final ListAllAdminsButton listAllAdminsButton;

    public RemoveAdminCommandHandler(AdminService adminService, ListAllAdminsButton listAllAdminsButton) {
        this.adminService = adminService;
        this.listAllAdminsButton = listAllAdminsButton;
    }

    @Override
    public String getCommand() {
        return "/remadmin";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    @Async
    public CompletableFuture<Void> process(long chatId, String text, TelegramClient telegramClient) {
        return CompletableFuture.runAsync(() -> {
           if (adminService.isAdminExist(String.valueOf(chatId))) {
               sendMessage(chatId, "Anda Bukan Admin", telegramClient);
               return;
           }
            Set<String> strings = adminService.countAllAdmin();
            if (strings.size() == 1) {
                sendMessage(chatId, "Tidak ada admin lain", telegramClient);
                return;
            }
            sendMessageWithMarkup(chatId,
                    "Silahkan Pilih Admin yang akan dihapus\n\n",
                    listAllAdminsButton.adminsButton(strings.stream().toList(), "rem"),
                    telegramClient);
        });
    }
}
