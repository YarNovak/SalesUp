package io.proj3ct.SpringDemoBot.service;

import io.proj3ct.SpringDemoBot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Send {



    private final Map<Person, Boolean> sent = new HashMap<>();

    public void clear() {
        sent.clear();
    }

    public boolean getOrDefault(Long chatId, boolean f, Long bot_id) {
        return sent.getOrDefault(new Person(chatId, bot_id), false);
    }

    public void put(Long chatId, boolean f, Long bot_id) {
        sent.put(new Person(chatId, bot_id), true);
    }

    public void remove(Long chatId, Long bot_id) {
        sent.remove(new Person(chatId, bot_id));
    }

}
