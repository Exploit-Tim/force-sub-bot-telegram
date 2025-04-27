package org.telegram.forcesub.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.forcesub.handler.CommandHandler;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private static final Logger log = LoggerFactory.getLogger(TelegramBot.class);
    private final String botUsername;

    private final String botToken;

    private final TelegramClient telegramClient;
    private final CommandHandler commandHandler;

    TelegramBot(@Value("${bot.username}") String botUsername, @Value("${bot.token}") String botToken, TelegramClient telegramClient, CommandHandler commandHandler) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.telegramClient = new OkHttpTelegramClient(botToken);
        this.commandHandler = commandHandler;
    }


    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        commandHandler.handle(update, telegramClient);
    }
}
