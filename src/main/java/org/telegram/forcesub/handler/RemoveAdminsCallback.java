package org.telegram.forcesub.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.forcesub.entity.Roles;
import org.telegram.forcesub.service.AdminService;
import org.telegram.forcesub.utils.ListAllAdminsButton;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Component
public class RemoveAdminsCallback implements CallbackProcessor{
    private final AdminService adminService;
    private final ListAllAdminsButton listAllAdminsButton;
    private final String ownerId;

    public RemoveAdminsCallback(AdminService adminService, ListAllAdminsButton listAllAdminsButton, @Value("${owner.userid}") String ownerId) {
        this.adminService = adminService;
        this.listAllAdminsButton = listAllAdminsButton;
        this.ownerId = ownerId;
    }

    @Override
    public String callbackData() {
        return "rem";
    }

    @Override
    public CompletableFuture<Void> process(Update update, TelegramClient telegramClient) {
        return CompletableFuture.runAsync(() -> {
            String[] callbackData = update.getCallbackQuery().getData().split("_");
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            String adminId = callbackData[1];
            if (chatId.equals(ownerId)) {
                if (adminId.equals(ownerId)) {
                    sendMessage(Long.parseLong(chatId), "Owner tidak bisa dihapus", telegramClient);
                    return;
                }
                adminService.deleteAdmin(adminId);
                Set<String> strings = adminService.countAllAdmin();
                EditMessageText editMessageText = EditMessageText.builder()
                        .text("Berhasil Hapus")
                        .chatId(Long.parseLong(chatId))
                        .messageId(update.getCallbackQuery().getMessage().getMessageId())
                        .build();
                try {
                    telegramClient.execute(editMessageText);
                    Thread.sleep(1_000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                editMessageWithMarkup(Long.parseLong(chatId), editMessageText.getMessageId(), "Pilih admin yang akan dihapus\n\n", telegramClient, listAllAdminsButton.adminsButton(strings.stream().toList(), "rem"));
                return;
            }
            sendMessage(Long.parseLong(chatId), "Admin List Tidak ada", telegramClient);
        });
    }
}
