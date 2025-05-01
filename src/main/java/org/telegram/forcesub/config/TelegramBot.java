package org.telegram.forcesub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.forcesub.handler.CommandHandler;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.concurrent.CompletableFuture;

@Component
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final String botToken;

    private final TelegramClient telegramClient;
    private final CommandHandler commandHandler;

    TelegramBot(@Value("${bot.token}") String botToken, CommandHandler commandHandler) {
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
        sendMessage(update, telegramClient);
    }

    @Async
    public CompletableFuture<Void> sendMessage(Update update, TelegramClient telegramClient) {
        return commandHandler.handle(update, telegramClient);
    }

}
