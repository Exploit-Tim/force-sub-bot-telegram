package org.telegram.forcesub.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.forcesub.entity.Message;
import org.telegram.forcesub.entity.MessageInformation;
import org.telegram.forcesub.service.GetChannelLinkService;
import org.telegram.forcesub.service.MessageService;
import org.telegram.forcesub.service.MessageTextService;
import org.telegram.forcesub.service.SubscriptionService;
import org.telegram.forcesub.utils.JoinButton;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class StartCommand implements CommandHandlerProcessor {

    private final SubscriptionService subscriptionService;
    private final GetChannelLinkService getChannelLinkService;
    private final JoinButton joinButton;
    private final String botUsername;
    private final String ownerUsername;

    private final MessageService messageService;
    private final MessageTextService messageTextService;

    public StartCommand(MessageService messageService,
                        SubscriptionService subscriptionService1,
                        GetChannelLinkService getChannelLinkService,
                        JoinButton joinButton,
                        @Value("${bot.username}") String botUsername,
                        @Value("${owner.username}") String ownerUsername, MessageTextService messageTextService) {
        this.messageService = messageService;
        this.subscriptionService = subscriptionService1;
        this.getChannelLinkService = getChannelLinkService;
        this.joinButton = joinButton;
        this.botUsername = botUsername;
        this.ownerUsername = ownerUsername;
        this.messageTextService = messageTextService;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getCommand() {
        return "/start";
    }

    @Override
    public CompletableFuture<Void> process(Update update, TelegramClient telegramClient) {
        return CompletableFuture.runAsync(() -> {
            if (update.getMessage().getText().equals("/start")){
                sendMessage(update.getMessage().getChatId(), replacePlaceHolder(MessageInformation.WELCOME, update), telegramClient);
                return;
            }
            Message message = messageService.getMessage(update.getMessage().getText().replace("/start", "").trim());
            if (message == null) {
                sendMessage(update.getMessage().getChatId(), "Kosong", telegramClient);
                return;
            }
            List<String> unsubcriptionUser = subscriptionService.getUnsubcriptionUser(String.valueOf(update.getMessage().getChatId()));
            for (String channel : unsubcriptionUser) {
                log.info("Unsubcription User: {}", channel);
            }
            List<String> channelLinks = getChannelLinkService.getChannelLinks(unsubcriptionUser);
            log.info("Channel Links: {}", channelLinks.size());
            if (!channelLinks.isEmpty()) {
                log.info("Channel Links: {}", channelLinks);
                String s = replacePlaceHolder(MessageInformation.START, update);
                log.info("Replace Placeholder: {}", s);
                sendMessageWithMarkup(update.getMessage().getChatId(), s, joinButton.joinButton(channelLinks), telegramClient);
                return;
            }
            log.info("Message: {}", message);
            copyMessage(message.getChatId(), message.getMessageId(), update.getMessage().getChatId(), telegramClient);
        });
    }
    public String replacePlaceHolder(MessageInformation messageInformation, Update update) {
        Map<String, String> field = new HashMap<>();

        String username = update.getMessage().getChat().getUserName();
        String firstName = update.getMessage().getChat().getFirstName();
        String lastName = update.getMessage().getChat().getLastName();

        field.put("{username}", username != null ? username : "");
        field.put("{userid}", update.getMessage().getChat().getId().toString());
        field.put("{firstname}", firstName != null ? firstName.trim() : "");
        field.put("{lastname}", lastName != null ? lastName.trim() : "");
        field.put("{botusername}", botUsername);
        field.put("{ownerusername}", "https://t.me/" + ownerUsername);

        log.info("Field: {}", field);
        String text = messageTextService.getMessageText(messageInformation);
        log.info("Text: {}", text);

        for (Map.Entry<String, String> entry : field.entrySet()) {
            String key = entry.getKey();
            text = text.replace(key, entry.getValue());
        }

        log.info("Text: {}", text);
        return text;
    }
}
