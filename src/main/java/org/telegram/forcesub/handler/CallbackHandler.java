package org.telegram.forcesub.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class CallbackHandler {

    private final Map<String, CallbackProcessor> callbackProcessorMap;


    @Autowired
    CallbackHandler(List<CallbackProcessor> callbackProcessors) {
        this.callbackProcessorMap = callbackProcessors.stream()
                .collect(Collectors.toMap(CallbackProcessor::callbackData, c -> c));
    }
    @Async
    public CompletableFuture<Void> handle(Update update, TelegramClient telegramClient) {
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData().split("_")[0];
            CallbackProcessor callbackProcessor = callbackProcessorMap.getOrDefault(callbackData, callbackProcessorMap.get("none"));
            callbackProcessor.process(update, telegramClient);
        }
        return CompletableFuture.completedFuture(null);
    }
}
