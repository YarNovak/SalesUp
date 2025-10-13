package io.proj3ct.SpringDemoBot.service;

import io.proj3ct.SpringDemoBot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Send {

    @Autowired
    private BotConfig config;

    private final Map<Person, Boolean> sent = new HashMap<>();

    public void clear() {
        sent.clear();
    }

    public boolean getOrDefault(Long chatId, boolean f) {
        return sent.getOrDefault(new Person(chatId, Long.valueOf(config.getBoit())), false);
    }

    public void put(Long chatId, boolean f) {
        sent.put(new Person(chatId, Long.valueOf(config.getBoit())), true);
    }

    public void remove(Long chatId) {
        sent.remove(new Person(chatId, Long.valueOf(config.getBoit())));
    }

}
