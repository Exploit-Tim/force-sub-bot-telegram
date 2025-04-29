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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Component
public class StartCommand implements CommandHandlerProcessor {

    private static final Logger log = LoggerFactory.getLogger(StartCommand.class);

    private final SubscriptionService subscriptionService;
    private final GetChannelLinkService getChannelLinkService;
    private final JoinButton joinButton;
    private final String botUsername;
    private final String ownerUsername;
    private final MessageService messageService;
    private final MessageTextService messageTextService;

    public StartCommand(MessageService messageService,
                        SubscriptionService subscriptionService,
                        GetChannelLinkService getChannelLinkService,
                        JoinButton joinButton,
                        @Value("${bot.username}") String botUsername,
                        @Value("${owner.username}") String ownerUsername,
                        MessageTextService messageTextService) {
        this.messageService = messageService;
        this.subscriptionService = subscriptionService;
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
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if ("/start".equals(messageText)) {
                sendHelpMessage(chatId, update, telegramClient);
                return;
            }

            handleStartWithPayload(chatId, messageText, update, telegramClient);
        });
    }

    private void sendHelpMessage(Long chatId, Update update, TelegramClient telegramClient) {
        String helpText = replacePlaceholders(MessageInformation.HELP, update);
        sendMessage(chatId, helpText, telegramClient);
    }

    private void handleStartWithPayload(Long chatId, String messageText, Update update, TelegramClient telegramClient) {
        String payload = messageText.replace("/start", "").trim();
        Message message = messageService.getMessage(payload);

        if (message == null) {
            sendMessage(chatId, "Kosong", telegramClient);
            return;
        }

        List<String> unsubscribedChannels = subscriptionService.getUnsubcriptionUser(chatId.toString());
        unsubscribedChannels.forEach(channel -> log.info("Unsubscribed User: {}", channel));

        List<String> channelLinks = getChannelLinkService.getChannelLinks(unsubscribedChannels);
        log.info("Found {} unsubscribed channel links", channelLinks.size());

        if (!channelLinks.isEmpty()) {
            String formattedText = replacePlaceholders(MessageInformation.START, update);
            String startUrl = String.format("https://t.me/%s?start=%s", botUsername, payload);
            sendMessageWithMarkup(chatId, formattedText, joinButton.joinButton(channelLinks, startUrl), telegramClient);
        } else {
            copyMessage(message.getChatId(), message.getMessageId(), chatId, telegramClient);
        }
    }

    private String replacePlaceholders(MessageInformation messageInfo, Update update) {
        Map<String, String> fields = new HashMap<>();

        var chat = update.getMessage().getChat();
        fields.put("{username}", Optional.ofNullable(chat.getUserName()).orElse(""));
        fields.put("{userid}", chat.getId().toString());
        fields.put("{firstname}", Optional.ofNullable(chat.getFirstName()).orElse("").trim());
        fields.put("{lastname}", Optional.ofNullable(chat.getLastName()).orElse("").trim());
        fields.put("{botusername}", botUsername);
        fields.put("{ownerusername}", "https://t.me/" + ownerUsername);

        String template = messageTextService.getMessageText(messageInfo);
        log.info("Replacing placeholders in: {}", template);

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            template = template.replace(entry.getKey(), entry.getValue());
        }

        return template;
    }
}
