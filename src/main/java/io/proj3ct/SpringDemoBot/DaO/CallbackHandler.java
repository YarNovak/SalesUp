package io.proj3ct.SpringDemoBot.DaO;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackHandler {

    boolean support(String callbackData);
    void handle(CallbackQuery query, TelegramLongPollingBot bot);

}
